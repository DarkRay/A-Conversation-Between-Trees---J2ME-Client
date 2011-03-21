package darkforest.mobile;

public class Configuration
{
	private String deviceID;
	
	private long interval;
	
	private String loggerBTName;
	
	private String loggerBTUrl;
	
	private int imageWidth;
	
	private int imageHeight;
	
	private int imageQuality;
	
	private String uploadUrl;
	
	public String getDeviceID()
	{
		return deviceID;
	}
	
	public void setDeviceID(String deviceID)
	{
		this.deviceID = deviceID;
	}

	public long getInterval()
	{
		return interval;
	}

	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	public String getLoggerBTName() 
	{
		return loggerBTName;
	}

	public void setLoggerBTName(String loggerBTName)
	{
		this.loggerBTName = loggerBTName;
	}

	public String getLoggerBTUrl()
	{
		return loggerBTUrl;
	}

	public void setLoggerBTUrl(String loggerBTUrl)
	{
		this.loggerBTUrl = loggerBTUrl;
	}

	public int getImageWidth()
	{
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) 
	{
		this.imageWidth = imageWidth;
	}

	public int getImageHeight()
	{
		return imageHeight;
	}

	public void setImageHeight(int imageHeight)
	{
		this.imageHeight = imageHeight;
	}

	public int getImageQuality()
	{
		return imageQuality;
	}

	public void setImageQuality(int imageQuality)
	{
		this.imageQuality = imageQuality;
	}

	public String getUploadUrl()
	{
		return uploadUrl;
	}

	public void setUploadUrl(String uploadUrl) 
	{
		this.uploadUrl = uploadUrl;
	}
}
