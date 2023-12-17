package error;

import error.errors.protocols.ErrorType;
import foundation.Logger;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler{
    private final List<ErrorType> errors;
    private final boolean enabled;

    public ErrorHandler(boolean enabled) {
        errors = new ArrayList<>();
        this.enabled = enabled;
    }

    public boolean enabled() {
        return enabled;
    }

    public void reportError(ErrorType error) {
        if (enabled) {
            errors.add(error);
            Logger.warn("\n" + error.detailedErrorMessage());
        }
    }

    public void reportFatalError(ErrorType error) throws FatalErrorException {
        if (enabled) {
            errors.add(error);
            Logger.error("\n" + error.detailedErrorMessage());
        }
        throw new FatalErrorException();
    }

    public String generateSimpleLog() {
        if (!enabled) {
            return "Error Handler is not enabled.";
        }

        final var logBuilder = new StringBuilder();
        for (final var error : errors) {
            logBuilder.append(error.simpleErrorMessage()).append('\n');
        }
        return logBuilder.toString();
    }

    public String generateDetailedLog() {
        if (!enabled) {
            return "Error handler is not enabled.";
        }

        final var logBuilder = new StringBuilder();
        for (final var error : errors) {
            logBuilder.append(error.detailedErrorMessage()).append('\n');
        }
        return logBuilder.toString();
    }
}
