package darkforest.mobile;

public interface ConfigurationDAO
{
	public Configuration loadConfiguration();

	public void saveConfiguration(Configuration configuration);
}