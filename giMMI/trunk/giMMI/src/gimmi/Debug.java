package gimmi;

import javax.servlet.ServletContext;

public class Debug {
	public static boolean debug = true;
	private static final String PREFIX = "DBG: ";
	private static ServletContext sContext = null;

	private static void out(boolean nl, String msg) {
		if (!Debug.debug) {
			return;
		}
		if (Debug.sContext != null) {
			// we should use the server log system
			if (nl) {
				Debug.sContext.log(msg);
			} else {
				Debug.sContext.log(msg + " [additional info may follow]");
			}
		} else {
			// log to console
			if (nl) {
				System.err.println(msg);
			} else {
				System.err.print(msg);
			}
		}
	}

	public static void print(final String message) {
		Debug.out(false, Debug.PREFIX + message);
	}

	public static void println(final String message) {
		Debug.out(true, Debug.PREFIX + message);
	}

	public static void println(final Class callerClass, final String message) {
		Debug.out(true, Debug.PREFIX + "[" + callerClass + "] " + message);
	}
}
