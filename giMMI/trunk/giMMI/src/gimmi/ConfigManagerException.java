package gimmi;

public class ConfigManagerException extends Exception {
	private static final long serialVersionUID = 1L;

	// general errors
	public static final String ERR_NOT_INITIALIZED = "You must initialize the ConfigManager before you can use it or request any instance.";
	public static final String ERR_CONFIG_INCOMPLETE = "There are missing configuration parameters (missing key is '%s'). This may prevent gimmi to run as expected.";
	public static final String ERR_CONFIGKEY_NOT_FOUND = "No configuration key named '%s' found.";

	String message;

	public ConfigManagerException() {
		super();
		this.message = "unknown";
	}

	public ConfigManagerException(String message) {
		super(message);
		this.message = message;
	}

	public ConfigManagerException(String message, Object[] replacement) {
		super(String.format(message, replacement));
		this.message = String.format(message, replacement);
	}

	public ConfigManagerException(String message, String replacement) {
		super(String.format(message, replacement));
		this.message = String.format(message, replacement);
	}
}
