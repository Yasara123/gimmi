package gimmi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigManager {
	private static ConfigManager instance = null;
	private static Properties conf = null;
	private static boolean initialized = false;
	private static Map<String, String> config = new HashMap<String, String>();

	/** essential config keys that are needed by gimmi */
	private static final String[] keys = new String[] {/*
	*/
			// database
			"database.name", // username used for db access
			"database.password", // password for the given db user
			"database.password", // password to access the db
	};

	public ConfigManager(Properties properties) {
		ConfigManager.conf = properties;

		// get all settings from the provided properties-file
		@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>) ConfigManager.conf
				.propertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String value = ConfigManager.conf.getProperty(key);
			Debug.println(this.getClass(), "Load config: " + key + " == '"
					+ value + "'");
			ConfigManager.config.put(key, value);
		}

		ConfigManager.initialized = true;
	}

	/**
	 * Check, if all needed configuration parameters are set.
	 */
	public void validate() throws ConfigManagerException {
		for (String key : ConfigManager.keys) {
			if (ConfigManager.config.get(key) == null) {
				throw new ConfigManagerException(
						ConfigManagerException.ERR_CONFIG_INCOMPLETE, key);
			}
		}
	}

	public String getByKey(String key) throws ConfigManagerException {
		if (ConfigManager.config.containsKey(key) == false) {
			throw new ConfigManagerException(
					ConfigManagerException.ERR_CONFIGKEY_NOT_FOUND, key);
		}

		return ConfigManager.config.get(key);
	}

	/**
	 * Get a ConfigManager instance
	 * 
	 * @return ConfigManager A ConfigManager instance
	 * @throws ConfigManagerException
	 */
	public static ConfigManager getInstance() throws ConfigManagerException {
		if (ConfigManager.initialized != true) {
			throw new ConfigManagerException(
					ConfigManagerException.ERR_NOT_INITIALIZED);
		}
		return ConfigManager.instance;
	}
}
