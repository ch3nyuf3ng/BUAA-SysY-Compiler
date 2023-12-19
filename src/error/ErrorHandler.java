package error;

import error.protocols.ErrorType;
import foundation.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ErrorHandler{
    private final Set<ErrorType> errors;
    private final boolean enabled;

    public ErrorHandler(boolean enabled) {
        errors = new HashSet<>();
        this.enabled = enabled;
    }

    public void reportError(ErrorType error) {
        if (enabled) {
            errors.add(error);
            Logger.error("\n" + error.detailedErrorMessage());
        }
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public String generateSimpleLog() {
        if (!enabled) {
            return "Error Handler is not enabled.";
        }

        final var logBuilder = new StringBuilder();
        final var sortedErrors = new ArrayList<>(errors);
        sortedErrors.sort(Comparator.comparingInt(ErrorType::lineNumber));
        for (final var error : sortedErrors) {
            logBuilder.append(error.simpleErrorMessage()).append('\n');
        }
        return logBuilder.toString();
    }
}
