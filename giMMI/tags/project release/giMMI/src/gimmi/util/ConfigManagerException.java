package gimmi.util;

/**
 * Custom exceptions for configuration manager
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class ConfigManagerException extends Exception {
	private static final long serialVersionUID = 1L;

	// general errors
	public enum Error implements GimmiExceptionMessage {
		NOT_INITIALIZED(
				"You must initialize the ConfigManager before you can use it or request any instance."), //
		CONFIG_INCOMPLETE(
				"There are missing configuration parameters (missing key is '%s'). This may prevent gimmi to run as expected."), //
		CONFIGKEY_NOT_FOUND("No configuration key named '%s' found.");

		private String msg;

		Error(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return this.msg;
		}
	}

	String message;

	public ConfigManagerException() {
		super();
		this.message = "unknown";
	}

	public ConfigManagerException(GimmiExceptionMessage message) {
		super(message.toString());
		this.message = message.toString();
	}

	public ConfigManagerException(GimmiExceptionMessage message,
			Object[] replacement) {
		super(String.format(message.toString(), replacement));
		this.message = String.format(message.toString(), replacement);
	}

	public ConfigManagerException(GimmiExceptionMessage message,
			String replacement) {
		super(String.format(message.toString(), replacement));
		this.message = String.format(message.toString(), replacement);
	}
}
