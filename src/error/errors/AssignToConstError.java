package error.errors;

import error.errors.protocols.ErrorType;

public record AssignToConstError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "h";
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
