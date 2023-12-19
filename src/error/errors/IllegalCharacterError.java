package error.errors;

import error.protocols.ErrorType;

public record IllegalCharacterError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "a";
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
