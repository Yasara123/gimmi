package gimmi.testing;

public abstract class Testing {
	protected static enum Format {
		HEADER, STEP, STEPSUB
	}

	/**
	 * Shorthand for system output.
	 * 
	 * @param message
	 *            Message to print
	 */
	protected static void so(String message, Format mType) {
		String msg;
		switch (mType) {
		case HEADER:
			msg = String.format("[%s]", message);
			break;
		case STEP:
			msg = String.format(" * %s", message);
			break;
		case STEPSUB:
			msg = String.format("   %s", message);
			break;
		default:
			msg = message;
			break;
		}
		System.out.println(msg);
	}

	/**
	 * Shorthand for error output. Will exit after throwing.
	 * 
	 * @param e
	 *            Exception to throw
	 */
	protected static void err(Exception e) {
		e.printStackTrace();
		System.exit(-1);
	}
}
