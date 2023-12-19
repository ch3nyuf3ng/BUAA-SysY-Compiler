package error.errors;

import error.protocols.ErrorType;

public record MissingRightBracketError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "k";
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
