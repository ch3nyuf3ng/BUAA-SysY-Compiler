package error.errors;

import error.errors.protocols.ErrorType;

public record IdentifierUndefinedError(
        int lineNumber
) implements ErrorType {
    @Override
    public String categoryCode() {
        return "c";
    }

    @Override
    public String simpleErrorMessage() {
        return lineNumber + categoryCode();
    }

    @Override
    public String detailedErrorMessage() {
        return simpleErrorMessage();
    }
}
