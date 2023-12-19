package error.errors;

import error.protocols.ErrorType;

public record MissingRightParenthesisError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "j";
    }

    @Override
    public String simpleErrorMessage() {
        return lineNumber + " " + categoryCode();
    }

    @Override
    public String detailedErrorMessage() {
        return simpleErrorMessage();
    }
}
