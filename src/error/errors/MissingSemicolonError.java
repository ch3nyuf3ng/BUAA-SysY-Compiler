package error.errors;

import error.errors.protocols.ErrorType;
import foundation.Position;

public record MissingSemicolonError(
        String line,
        Position position
) implements ErrorType {
    @Override
    public String categoryCode() {
        return "i";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position().lineNumber();
    }

    public String detailedErrorMessage() {
        final var whitespaces = " ".repeat(
                String.valueOf(position().lineNumber()).length()
                        + 1
                        + position().columnNumber()
        );
        return position().lineNumber() + ' ' + line + '\n'
                + whitespaces + '^' + '\n'
                + "Missing a semicolon." + '\n';
    }
}
