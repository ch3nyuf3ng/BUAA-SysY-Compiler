package foundation;

import java.util.Set;

public class Logger {
    public enum Category {
        LEXER, PARSER, SYMBOL, IR, INTERPRETER, TEST
    }

    public static final boolean LogEnabled = false;
    public static final boolean ErrorOutputEnabled = false;
    public static final boolean WarningOutputEnabled = false;
    public static final boolean DebugOutputEnabled = false;
    private static final StringBuilder logBuilder = new StringBuilder();
    public static final Set<Category> DebugOutputAllowedCategories = Set.of(
            Category.LEXER,
            Category.PARSER,
            Category.SYMBOL,
            Category.IR,
            Category.INTERPRETER
    );

    public static void error(String errorMessage) {
        if (LogEnabled) {
            if (ErrorOutputEnabled) {
                System.out.println("[ERROR] " + errorMessage);
            }
            logBuilder.append("[ERROR] ").append(errorMessage).append('\n');
        }
    }

    public static void warn(String warningMessage) {
        if (LogEnabled) {
            if (WarningOutputEnabled) {
                System.out.println("[WARN] " + warningMessage);
            }
            logBuilder.append("[WARN] ").append(warningMessage).append('\n');
        }
    }

    public static void debug(String infoMessage, Category category) {
        if (LogEnabled && DebugOutputAllowedCategories.contains(category)) {
            if (DebugOutputEnabled) {
                System.out.println("[DEBUG] " + infoMessage);
            }
            logBuilder.append("[DEBUG] ").append(infoMessage).append('\n');
        }
    }

    public static void writeLogFile() {
        if (LogEnabled) {
            IO.simpleOutputToFolder("logs", "log.txt", logBuilder.toString());
        }
    }
}
