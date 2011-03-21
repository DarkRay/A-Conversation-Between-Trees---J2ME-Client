package darkforest.mobile;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class ServerDiscoveryService
implements DiscoveryListener
{	
	private final Object inquiryCompleteEvent = new Object();
	
	private final Object serviceSearchSync = new Object();
	
	private final UUID serviceUUID;
	
	private Hashtable devicesDiscovered;
	
	private Vector servicesDiscovered;
	
	private volatile boolean serviceSearchInProgress = false;
	
	private volatile int serviceSearchID;
	
	public ServerDiscoveryService(UUID serviceUUID)
	{
		this.serviceUUID = serviceUUID;
	}
	
	public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) 
	{
		//#debug info
		System.out.println("Found remote device with address " + device.getBluetoothAddress());
		try { devicesDiscovered.put(device.getFriendlyName(false), device); } 
		catch(Exception e) 
		{
			//#mdebug info
			System.out.println("Could not get the remote device's friendly name: " + e.getMessage());
			e.printStackTrace();
			//#enddebug
		}
	}

	public void inquiryCompleted(int type) 
	{
		//#debug info
		System.out.println("Completed device inquiry with completion type: " + type);
		synchronized(inquiryCompleteEvent) 
		{
			inquiryCompleteEvent.notifyAll();
		}
	}
	
	public void servicesDiscovered(int transactionID, ServiceRecord[] serviceRecords) 
	{
		for(int i = 0; i < serviceRecords.length; i++)
			servicesDiscovered.addElement(serviceRecords[i]);
	}

	public void serviceSearchCompleted(int transactionID, int responseCode) 
	{
		//#mdebug info
		System.out.println("Completed service search with transaction ID " 
				+ transactionID + " and response code " + responseCode);
		//#enddebug
		synchronized(inquiryCompleteEvent) 
		{
			inquiryCompleteEvent.notifyAll();
		}
	}
	
	/**
	 * @return <Device Name, Service URL> 
	 */
	public synchronized Hashtable getAvailableServers() 
	throws 	BluetoothStateException,
			InterruptedException
	{
		serviceSearchInProgress = false;
		devicesDiscovered = new Hashtable();
		servicesDiscovered = new Vector();
	
		DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		synchronized(inquiryCompleteEvent) 
		{
			discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
			inquiryCompleteEvent.wait();
		}
		
		
		Hashtable availableServers = new Hashtable();
		UUID[] searchUuidSet = new UUID[] { serviceUUID };
		for(Enumeration e = devicesDiscovered.keys(); e.hasMoreElements(); )
		{
			servicesDiscovered.removeAllElements();
			String name = (String)e.nextElement();
			RemoteDevice rd = (RemoteDevice)devicesDiscovered.get(name);
			//#debug info
			System.out.println("Service search initiated on remote device: " + name);
			synchronized(inquiryCompleteEvent) 
	        {
				synchronized(serviceSearchSync)
				{
					serviceSearchID = discoveryAgent.searchServices(null, searchUuidSet, rd, this);
					serviceSearchInProgress = true;
				}
				//#debug info
				System.out.println("Service search started with transaction ID " + serviceSearchID);
				inquiryCompleteEvent.wait();
				serviceSearchInProgress = false;
	        }
			
			for(Enumeration en = servicesDiscovered.elements(); en.hasMoreElements(); )
			{
				ServiceRecord serviceRecord = (ServiceRecord)en.nextElement();				
				String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url == null)
                    continue;
                
                //#debug info
                System.out.println("Service found: " + url);
                availableServers.put(name, url);
                break;
			}
		}

		return availableServers;
	}
	
	public void cancelInquiry()
	throws BluetoothStateException
	{
		DiscoveryAgent discoveryAgent = 
				LocalDevice.getLocalDevice().getDiscoveryAgent();
		discoveryAgent.cancelInquiry(this);
		synchronized(serviceSearchSync)
		{
			if(serviceSearchInProgress)
				discoveryAgent.cancelServiceSearch(serviceSearchID);
		}
	}
}
