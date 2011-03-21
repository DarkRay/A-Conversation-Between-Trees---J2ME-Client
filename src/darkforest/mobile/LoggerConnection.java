package darkforest.mobile;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class LoggerConnection
{
	private final Configuration configuration;
	
	private boolean connected = false;
	
	private StreamConnection connection;
	
	private Logger logger;
	
	public LoggerConnection(Configuration configuration)
	{
		this.configuration = configuration;
	}
	
	public Hashtable getLoggerData()
	{
		if(!connected)
			try
			{
				connect();
			}
			catch(IOException e)
			{
				return null;
			}
			
		try
		{
			Hashtable data = new Hashtable();
			
			StringBuffer buf = new StringBuffer();
			int[] sensors = logger.readSensors();
			for(int i = 0; i < sensors.length; i++)
				buf.append(sensors[i]).append(":");
			data.put("sensors", buf.toString().substring(0, buf.length() - 1));
			
			buf.setLength(0);
			int[] ranges = logger.readRanges();
			for(int i = 0; i < ranges.length; i++)
				buf.append(ranges[i]).append(":");
			data.put("ranges", buf.toString().substring(0, buf.length() - 1));
			
			buf.setLength(0);
			int[] values = logger.readValues();
			for(int i = 0; i < values.length; i++)
				buf.append(values[i]).append(":");
			data.put("values", buf.toString().substring(0, buf.length() - 1));
			
			return data;
		}
		catch(IOException e)
		{
			try
			{
				disconnect();
			}
			catch(IOException ex)
			{
				connected = false;
			}
			return null;
		}
	}
	
	private synchronized void connect() 
	throws IOException
	{
		connection = (StreamConnection)Connector.open(configuration.getLoggerBTUrl());
		try
		{
			logger = new Logger(
					connection.openDataInputStream(),
					connection.openDataOutputStream());
			connected = true;
		}
		catch(IOException e)
		{
			connected = false;
			connection.close();
			throw e;
		}
	}
	
	private synchronized void disconnect() 
	throws IOException
	{
		connected = false;
		if(connection != null)
			connection.close();
		logger = null;
		connection = null;
	}
}
