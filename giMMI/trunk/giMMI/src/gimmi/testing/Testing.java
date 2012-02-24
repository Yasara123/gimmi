package gimmi.testing;

import java.util.Random;

public abstract class Testing {
	protected static enum Format {
		PLAIN, HEADER, HEADERSUB, STEP, STEPSUB, STEPINFO, STEPFINAL
	}

	protected static int randomInt(int max) {
		return (int) (max * Math.random()) + 1;
	}

	protected static String randomString(int length) {
		Random ran = new Random();
		char data = ' ';
		String dat = "";
		for (int i = 0; i <= length; i++) {
			data = (char) (ran.nextInt(25) + 97);
			dat = data + dat;
		}
		return dat;
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
			msg = String.format("\n==========[%s]==========", message);
			break;
		case HEADERSUB:
			msg = String.format("%s\n----------------------------------------",
					message);
			break;
		case STEP:
			msg = String.format(" * %s", message);
			break;
		case STEPSUB:
			msg = String.format("   %s", message);
			break;
		case STEPINFO:
			msg = String.format("   >> %s", message);
			break;
		case STEPFINAL:
			msg = String.format(" ### %s", message);
			break;
		case PLAIN:
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
