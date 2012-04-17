package gimmi.util;

public class Debug {
    public static boolean debug = true;
    private static final String PREFIX = "DBG: ";

    public static void print(final String message) {
	Log.out(false, Debug.PREFIX, message);
    }

    public static void println(final String message) {
	Log.out(true, Debug.PREFIX, message);
    }

    public static void
	    println(final Class<?> callerClass, final String message) {
	Log.out(true, Debug.PREFIX, "[" + callerClass + "] " + message);
    }
}
