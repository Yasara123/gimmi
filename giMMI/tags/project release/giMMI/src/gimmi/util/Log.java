package gimmi.util;

import javax.servlet.ServletContext;

public class Log {
    public static boolean debug = true;
    protected static final String PREFIX = "";
    private static ServletContext sContext = null;

    protected static void out(boolean nl, String prefix, String msg) {
	msg = prefix + msg;
	if (!Debug.debug) {
	    return;
	}
	if (Log.sContext != null) {
	    // we should use the server log system
	    if (nl) {
		Log.sContext.log(msg);
	    } else {
		Log.sContext.log(msg + " [additional info may follow]");
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

    /**
     * Simply print to log output
     * 
     * @param message
     *            The message to print
     */
    public static void print(final String message) {
	Log.out(false, Log.PREFIX, message);
    }

    /**
     * Simply print a line to log output
     * 
     * @param message
     *            The message to print
     */
    public static void println(final String message) {
	Log.out(true, Log.PREFIX, message);
    }

    /**
     * Simply print a line to log output
     * 
     * @param callerClass
     *            The calling class
     * @param message
     *            The message to print
     */
    public static void
	    println(final Class<?> callerClass, final String message) {
	Log.out(true, Log.PREFIX, "[" + callerClass + "] " + message);
    }
}
