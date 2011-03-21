package darkforest.mobile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class FileConfigurationDAO 
implements ConfigurationDAO
{
	private static final String CONFIG_FILE = "file:///e:/darkforest/darkforest.config";
	
	private static final char DELIMETER = '|';
	
	private static final Configuration DEFAULT_CONFIGURATION = new Configuration();
	static
	{
		DEFAULT_CONFIGURATION.setDeviceID("unknown");
		DEFAULT_CONFIGURATION.setImageHeight(480);
		DEFAULT_CONFIGURATION.setImageQuality(75);
		DEFAULT_CONFIGURATION.setImageWidth(640);
		DEFAULT_CONFIGURATION.setInterval(60);
		DEFAULT_CONFIGURATION.setLoggerBTName("Unknown");
		DEFAULT_CONFIGURATION.setLoggerBTUrl("Unknown");
		DEFAULT_CONFIGURATION.setUploadUrl(
				"http://kerouac.mrl.nott.ac.uk/darkforest/server/upload-j2me.html");
		
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
	}
	
	/* (non-Javadoc)
	 * @see darkforest.mobile.ConfigurationDAO#loadConfiguration()
	 */
	public Configuration loadConfiguration()
	{
		FileConnection configFile;
		try
		{
			configFile = (FileConnection)Connector.open(CONFIG_FILE);
		}
		catch(IOException e)
		{
			return DEFAULT_CONFIGURATION;
		}
		
		try
		{		
			if(!configFile.exists())
				return DEFAULT_CONFIGURATION;
			InputStreamReader in = new InputStreamReader(configFile.openInputStream());
			SimpleReaderTokenizer tok = new SimpleReaderTokenizer(in, DELIMETER);	
			Configuration config = new Configuration();
			config.setDeviceID(tok.getNextToken());
			config.setImageHeight(Integer.parseInt(tok.getNextToken()));
			config.setImageQuality(Integer.parseInt(tok.getNextToken()));
			config.setImageWidth(Integer.parseInt(tok.getNextToken()));
			config.setInterval(Long.parseLong(tok.getNextToken()));
			config.setLoggerBTName(tok.getNextToken());
			config.setLoggerBTUrl(tok.getNextToken());
			config.setUploadUrl(tok.getNextToken());
			
			in.close();
			
			return config;
		}
		catch(IOException e) 
		{
			return DEFAULT_CONFIGURATION;
		}
		finally
		{
			try
			{
				configFile.close();
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see darkforest.mobile.ConfigurationDAO#saveConfiguration(darkforest.mobile.Configuration)
	 */
	public void saveConfiguration(Configuration configuration)
	{
		FileConnection configFile;
		try
		{
			configFile = (FileConnection)Connector.open(CONFIG_FILE);
			if(configFile.exists())
				configFile.delete();
			configFile.create();
			
		}
		catch(IOException e)
		{
			return;
		}
		
		try
		{			
			Writer out = new OutputStreamWriter(configFile.openOutputStream());
			out.write(configuration.getDeviceID());
			out.write("|");
			out.write(String.valueOf(configuration.getImageHeight()));
			out.write("|");
			out.write(String.valueOf(configuration.getImageQuality()));
			out.write("|");
			out.write(String.valueOf(configuration.getImageWidth()));
			out.write("|");
			out.write(String.valueOf(configuration.getInterval()));
			out.write("|");
			out.write(configuration.getLoggerBTName());
			out.write("|");
			out.write(configuration.getLoggerBTUrl());
			out.write("|");
			out.write(configuration.getUploadUrl());
			out.close();
		}
		catch(IOException e) 
		{
			return;
		}
		finally
		{
			try
			{
				configFile.close();
			}
			catch(IOException e)
			{
				
			}
		}
	}
}
