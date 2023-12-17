package error.errors;

import error.errors.protocols.ErrorType;

public record MissingReturnError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "g";
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
