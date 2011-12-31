package gimmi.util;

import java.io.FileInputStream;
import java.io.IOException;
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

	public static void loadProperties(Properties properties) {
		ConfigManager.conf = properties;

		// get all settings from the provided properties-file
		@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>) ConfigManager.conf
				.propertyNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String value = ConfigManager.conf.getProperty(key);
			ConfigManager.config.put(key, value);
		}

		ConfigManager.initialized = true;
	}

	/**
	 * Check, if all needed configuration parameters are set.
	 */
	public static void validate() throws ConfigManagerException {
		for (String key : ConfigManager.keys) {
			if (ConfigManager.config.get(key) == null) {
				throw new ConfigManagerException(
						ConfigManagerException.Error.CONFIG_INCOMPLETE, key);
			}
		}
	}

	public static String getByKey(String key) throws ConfigManagerException {
		if (ConfigManager.config.containsKey(key) == false) {
			throw new ConfigManagerException(
					ConfigManagerException.Error.CONFIGKEY_NOT_FOUND, key);
		}

		return ConfigManager.config.get(key);
	}

	public static boolean isInitialized() {
		return ConfigManager.initialized;
	}

	public static void tryLoadFallbackConfig() {
		Properties prop;
		String path;

		Log.println(ConfigManager.class,
				"Try to load a configuration from the fallback locations.");

		// local run
		path = "conf/gimmi.properties";
		Log.println(ConfigManager.class, "Trying to read from " + path + "");
		prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
			ConfigManager.loadProperties(prop);
			Log.println("success!");
			return;
		} catch (IOException e) {
			Log.println("failed!");
		}

		// web run
		path = "WEB-INF/classes/gimmi.properties";
		Log.println(ConfigManager.class, "Trying to read from " + path + "");
		prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
			ConfigManager.loadProperties(prop);
			Log.println("success!");
			return;
		} catch (IOException e) {
			Log.println("failed!");
		}
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
					ConfigManagerException.Error.NOT_INITIALIZED);
		}
		return ConfigManager.instance;
	}
}
