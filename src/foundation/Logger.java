package foundation;

import foundation.IO;

public class Logger {
    public static final boolean LogEnabled = true;
    public static final boolean ErrorOutputEnabled = false;
    public static final boolean WarningOutputEnabled = false;
    public static final boolean InfoOutputEnabled = true;
    private static final StringBuilder stringBuilder = new StringBuilder();

    public static void error(String errorMessage) {
        if (LogEnabled) {
            if (ErrorOutputEnabled) System.out.println("[ERROR] " + errorMessage);
            stringBuilder.append("[ERROR] ").append(errorMessage).append('\n');
        }
    }

    public static void warn(String warningMessage) {
        if (LogEnabled) {
            if (WarningOutputEnabled) System.out.println("[WARN] " + warningMessage);
            stringBuilder.append("[WARN] ").append(warningMessage).append('\n');
        }
    }

    public static void info(String infoMessage) {
        if (LogEnabled) {
            if (InfoOutputEnabled) System.out.println("[INFO] " + infoMessage);
            stringBuilder.append("[INFO] ").append(infoMessage).append('\n');
        }
    }

    public static void writeLogFile() {
        if (LogEnabled) {
            IO.outputResult("logs", "log.txt", stringBuilder.toString());
        }
    }
}
