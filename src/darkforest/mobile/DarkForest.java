package darkforest.mobile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class DarkForest 
extends MIDlet
implements CommandListener
{	
	private static final String BACKUP_FILE = "file:///e:/darkforest/backup.bin";
	
	private static final Command exitCommand = new Command("Exit", Command.EXIT, 1);
	
	private static final Command changeDeviceIDCommand = new Command("Change Device ID", Command.SCREEN, 2);
	
	private static final Command changeDeviceCommand = new Command("Change Logger", Command.SCREEN, 10);
	
	private static final Command changeServerCommand = new Command("Change Server", Command.SCREEN, 50);
	
	private static final Command changeImageCommand = new Command("Image Settings", Command.SCREEN, 50);
	
	private static final Command changeIntervalCommand = new Command("Change Interval", Command.SCREEN, 50);
	
	private static final Command startCommand = new Command("Start", Command.SCREEN, 5);
	
	private static final UUID BLUETOOTH_SPP_UUID = new UUID(0x1101);
	
	private static final Form form = new Form("DarkForest");
	
	private static final StringItem announce = new StringItem("", "Application started");
	
	private boolean started = false;
	
	private Hashtable availableServers;
	
	private Configuration configuration;
	
	private OutputStream backupFileOut;
	
	protected void destroyApp(boolean force)
	throws MIDletStateChangeException {}

	protected void pauseApp(){}

	protected void startApp()
	throws MIDletStateChangeException 
	{
		if(started)
		{
			Display.getDisplay(this).setCurrent(form);
			return;
		}
		
		try
		{
			FileConnection dir = (FileConnection)Connector.open("file:///e:/darkforest");
			if(!dir.exists())
				dir.mkdir();
			dir.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Couldn't create configuration directory");
		}
		
		try
		{
			FileConnection backupFile = (FileConnection)Connector.open(BACKUP_FILE);
			if(!backupFile.exists())
				backupFile.create();
			backupFileOut = backupFile.openOutputStream();
			
		}
		catch(IOException e)
		{
			backupFileOut = null;
		}
		
		form.setCommandListener(this);
		form.addCommand(exitCommand);
		form.addCommand(changeDeviceIDCommand);
		form.addCommand(changeDeviceCommand);
		form.addCommand(changeServerCommand);
		form.addCommand(changeImageCommand);
		form.addCommand(changeIntervalCommand);
		form.addCommand(startCommand);
		ConfigurationDAO dao = new FileConfigurationDAO();
		configuration = dao.loadConfiguration();
		
		started = true;
		Display.getDisplay(this).setCurrent(form);
		form.append(announce);
	}
	
	public void commandAction(Command command, Displayable screen) 
	{
		if(command == exitCommand)
		{
			if(backupFileOut != null)
				try 
				{
					backupFileOut.close();
				}
				catch(IOException e){ }
			notifyDestroyed();
			System.exit(0);
			return;
		}
		if(command == changeDeviceIDCommand)
		{
			changeDeviceID();
			return;
		}
		if(command == changeDeviceCommand)
		{
			changeDevice();
			return;
		}
		if(command == changeImageCommand)
		{
			changeImageParameters();
			return;
		}
		if(command == changeIntervalCommand)
		{
			changeInterval();
			return;
		}
		if(command == changeServerCommand)
		{
			changeServer();
			return;
		}
		if(command == startCommand)
		{
			start();
			return;
		}
	}
	
	private void changeImageParameters()
	{
		Form paramForm = new Form("Change image parameters");
		final TextField widthField = new TextField(
				"Width", String.valueOf(configuration.getImageWidth()), 10, TextField.NUMERIC);
		final TextField heightField = new TextField(
				"Height", String.valueOf(configuration.getImageHeight()), 10, TextField.NUMERIC);
		final TextField qualityField = new TextField(
				"Quality", String.valueOf(configuration.getImageQuality()), 10, TextField.NUMERIC);
		paramForm.append(widthField);
		paramForm.append(heightField);
		paramForm.append(qualityField);
		paramForm.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				configuration.setImageWidth(Integer.parseInt(widthField.getString()));
				configuration.setImageHeight(Integer.parseInt(heightField.getString()));
				configuration.setImageQuality(Integer.parseInt(qualityField.getString()));
				new FileConfigurationDAO().saveConfiguration(configuration);
			}
		});
		Display.getDisplay(this).setCurrent(paramForm);
	}
	
	private void changeDeviceID()
	{
		Form didForm = new Form("Change device ID");
		final TextField field = new TextField("Device ID", configuration.getDeviceID(), 255, 0);
		didForm.addCommand(new Command("Save", Command.SCREEN, 1));
		didForm.append(field);
		didForm.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				configuration.setDeviceID(field.getString());
				new FileConfigurationDAO().saveConfiguration(configuration);
				Display.getDisplay(DarkForest.this).setCurrent(form);
			}
		});
		Display.getDisplay(this).setCurrent(didForm);
	}
	
	private void changeServer()
	{
		Form didForm = new Form("Change upload server");
		final TextField field = new TextField("Upload URL", configuration.getUploadUrl(), 255, 0);
		didForm.addCommand(new Command("Save", Command.SCREEN, 1));
		didForm.append(field);
		didForm.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				configuration.setUploadUrl(field.getString());
				new FileConfigurationDAO().saveConfiguration(configuration);
				Display.getDisplay(DarkForest.this).setCurrent(form);
			}
		});
		Display.getDisplay(this).setCurrent(didForm);
	}
	
	private void changeInterval()
	{
		Form didForm = new Form("Change interval");
		final TextField field = new TextField("Interval (Seconds)", String.valueOf(configuration.getInterval()), 255, TextField.NUMERIC);
		didForm.addCommand(new Command("Save", Command.SCREEN, 1));
		didForm.append(field);
		didForm.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) {
				configuration.setInterval(Long.parseLong(field.getString()));
				new FileConfigurationDAO().saveConfiguration(configuration);
				Display.getDisplay(DarkForest.this).setCurrent(form);
			}
		});
		Display.getDisplay(this).setCurrent(didForm);
	}
	
	private void start()
	{
		new Thread(new Runnable() 
		{	
			public void run() 
			{
				Camera camera = new Camera(form);
				LoggerConnection logger = new LoggerConnection(configuration);
				while(true)
				{
					try
					{
						announce.setText("About to take photo");
						byte[] image = camera.getSnapshot();
						if(image == null)
							form.insert(1, new StringItem("", "Unable to get an image"));
						announce.setText("About to get datalogger data");
						Hashtable loggerData = logger.getLoggerData();
						if(loggerData == null)
							form.insert(1, new StringItem("", "Unable to get logger data"));
						try 
						{
							announce.setText("About to upload data");
							HttpMultipartRequest request = new HttpMultipartRequest();
							request.addField("device", configuration.getDeviceID());
							request.addField("timestamp", String.valueOf(System.currentTimeMillis()));
							if(loggerData != null)
								for(Enumeration e = loggerData.keys(); e.hasMoreElements();)
								{
									String key = (String)e.nextElement();
									request.addField(key, (String)loggerData.get(key));
								}
							if(image != null)
								request.addFile("image", "image.jpg", "image/jpeg", image);
							HttpMultipartRequestSender sender = new HttpMultipartRequestSender(configuration.getUploadUrl(), request);
							if(sender.doRequest() != HttpConnection.HTTP_OK)
								recordToFile(image, loggerData);
						}
						catch(Exception e)
						{
							form.insert(1, new StringItem("", "Unable to connect to server"));
							recordToFile(image, loggerData);
						}
						announce.setText("Running");
						Thread.sleep(configuration.getInterval() * 1000);
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}).start();
	}
	
	private void changeDevice()
	{
		final ServerDiscoveryService discoveryService = new ServerDiscoveryService(BLUETOOTH_SPP_UUID);
		Thread t = new Thread(new Runnable(){
			public void run()
			{
				try 
				{
					availableServers = discoveryService.getAvailableServers();					
					if(availableServers == null || availableServers.size() == 0)
					{
						Alert noServersAlert = new Alert(
								"No servers",
								"No servers found", 
								null,
								AlertType.INFO);
						Display.getDisplay(DarkForest.this).setCurrent(noServersAlert, form);
						return;
					}
					showDeviceSelection();
				} 
				catch(Exception e) 
				{
					//#mdebug error
					System.out.println("Could not get the available servers: " + e.getMessage());
					e.printStackTrace();
					//#enddebug
					Alert errorAlert = new Alert(
							"Error",
							e.toString() + " : " + e.getMessage(), 
							null,
							AlertType.INFO);
					Display.getDisplay(DarkForest.this).setCurrent(errorAlert, form);
				}
			}
		});
		t.start();
	}
	
	private void showDeviceSelection()
	{
		final Command selectCommand = new Command("Select", Command.ITEM, 1);
		Form deviceForm = new Form("Select Logger");
		deviceForm.addCommand(new Command("Cancel", Command.SCREEN, 1));
		deviceForm.setCommandListener(new CommandListener() {
			public void commandAction(Command arg0, Displayable arg1) 
			{
				Display.getDisplay(DarkForest.this).setCurrent(form);
			}
		});
		final ChoiceGroup serverSelectionItem = new ChoiceGroup(
				"Select server", Choice.EXCLUSIVE);
		for(Enumeration e = availableServers.keys(); e.hasMoreElements(); )
			serverSelectionItem.append((String)e.nextElement(), null);
		serverSelectionItem.addCommand(selectCommand);
		serverSelectionItem.setDefaultCommand(selectCommand);
		deviceForm.append(serverSelectionItem);
		serverSelectionItem.setItemCommandListener(new ItemCommandListener() {
			
			public void commandAction(Command arg0, Item arg1) 
			{
				String name = serverSelectionItem.getString(serverSelectionItem.getSelectedIndex());
				String url = (String)availableServers.get(name);
				configuration.setLoggerBTName(name);
				configuration.setLoggerBTUrl(url);
				new FileConfigurationDAO().saveConfiguration(configuration);
				Display.getDisplay(DarkForest.this).setCurrent(form);
			}
		});
		Display.getDisplay(this).setCurrent(deviceForm);
	}
	
	private void recordToFile(byte[] image, Hashtable loggerData)
	{
		if(backupFileOut == null)
			return;
		
		try
		{
			out("{");
			out(String.valueOf(System.currentTimeMillis()));
			out(",");
			if(loggerData == null)
				out("null");
			else
			{
				out("{");
				for(Enumeration e = loggerData.keys(); e.hasMoreElements();)
				{
					String key = (String)e.nextElement();
					String value = (String)loggerData.get(key);
					out(key + ":" + value + ",");
				}
				out("}");
			}
			out(",");
			if(image == null)
				out("null");
			else
			{
				out("{");
				out(String.valueOf(image.length));
				out(",");
				backupFileOut.write(image);
				out("}");
			}
			out("}");
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void out(String s)
	throws Exception
	{
		backupFileOut.write(s.getBytes());
	}
}
