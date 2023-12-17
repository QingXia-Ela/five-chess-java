package src.Logger;

public class Logger {
    public static boolean isLoggingEnabled = true;
    private static int logLevel = 4;

    public static void setLogLevel(int logLevel) {
        Logger.logLevel = logLevel;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void info(String message) {
        if (isLoggingEnabled && logLevel >= 3) System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        if (isLoggingEnabled && logLevel >= 1)
            System.out.println("[ERROR] " + message);
    }

    public static void warning(String message) {
        if (isLoggingEnabled && logLevel >= 2)
            System.out.println("[WARNING] " + message);
    }

    public static void debug(String message) {
        if (isLoggingEnabled && logLevel >= 4)
            System.out.println("[DEBUG] " + message);
    }
}
