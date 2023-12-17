package error.errors;

import error.errors.protocols.ErrorType;

public record FuncArgTypeUnmatchError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "e";
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
