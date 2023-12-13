package src.Logger;

public class Logger {
    public static boolean isLoggingEnabled = true;
    public static void info(String message) {
        if (isLoggingEnabled) System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        if (isLoggingEnabled)
            System.out.println("[ERROR] " + message);
    }

    public static void warning(String message) {
        if (isLoggingEnabled)
            System.out.println("[WARNING] " + message);
    }

    public static void debug(String message) {
        if (isLoggingEnabled)
            System.out.println("[DEBUG] " + message);
    }
}
