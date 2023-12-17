package error.errors;

import error.errors.protocols.ErrorType;

public record ExcessReturnError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "f";
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
