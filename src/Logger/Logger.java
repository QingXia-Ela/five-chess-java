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
        if (isLoggingEnabled && logLevel >= 3) System.out.println("\033[34;4m"+"[INFO]"+"\033[0m " + message);
    }

    public static void error(String message) {
        if (isLoggingEnabled && logLevel >= 1)
            System.out.println("\033[31;4m"+"[ERROR]"+"\033[0m " + message);
    }

    public static void warning(String message) {
        if (isLoggingEnabled && logLevel >= 2)
            System.out.println("\033[33;4m"+ "[WARNING]" +"\033[0m " + message);
    }

    public static void debug(String message) {
        if (isLoggingEnabled && logLevel >= 4)
            System.out.println("\033[32;4m"+"[DEBUG]"+"\033[0m " + message);
    }
}
