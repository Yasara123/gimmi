package gimmi.database;

import gimmi.util.GimmiExceptionMessage;

/**
 * Custom exception for the corpus database
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class CorpusDatabaseException extends Exception {
	private static final long serialVersionUID = 1L;

	// predefined messages
	public enum Error implements GimmiExceptionMessage {
		COLUMN_NOT_FOUND("Database field '%s' does not exist."), //
		COLUMN_NOT_FOUND_IN("Database field '%s' does not exist in table '%s'."), //
		FIELDS_MISSING(
				"Not all required fields (namely: %s) where specified. You gave: %s"), //
		FIELDTYPE_UNKNOWN("The field type you specified is unknown (was '%s')."), //
		VALUE_NOT_FOUND("%s could not be found in the database.");

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

	/**
	 * Empty constructor without any message
	 */
	public CorpusDatabaseException() {
		super();
		this.message = "unknown";
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message to show with this exception
	 */
	public CorpusDatabaseException(String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Constructor
	 * 
	 * @param geMessage
	 *            Message to show with this exception
	 */
	public CorpusDatabaseException(GimmiExceptionMessage geMessage) {
		super(geMessage.toString());
		this.message = geMessage.toString();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message to show with this exception
	 * @param replacement
	 *            Data to replace multiple format expressions in message
	 */
	public CorpusDatabaseException(String message, Object... replacement) {
		super(String.format(message, replacement));
		this.message = String.format(message, replacement);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message to show with this exception
	 * @param replacement
	 *            String to replace a format expression in message
	 */
	public CorpusDatabaseException(String message, String replacement) {
		super(String.format(message, replacement));
		this.message = String.format(message, replacement);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message to show with this exception
	 * @param replacement
	 *            Data to replace multiple format expressions in message
	 */
	public CorpusDatabaseException(GimmiExceptionMessage geMessage,
			Object... replacement) {
		super(String.format(geMessage.toString(), replacement));
		this.message = String.format(geMessage.toString(), replacement);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Message to show with this exception
	 * @param replacement
	 *            String to replace a format expression in message
	 */
	public CorpusDatabaseException(GimmiExceptionMessage geMessage,
			String replacement) {
		super(String.format(geMessage.toString(), replacement));
		this.message = String.format(geMessage.toString(), replacement);
	}
}
