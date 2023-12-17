package error.errors;

import error.errors.protocols.ErrorType;

public record IdentifierRedefinedError(
        int lineNumber
) implements ErrorType {
    @Override
    public String categoryCode() {
        return "b";
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
