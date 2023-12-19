package error.errors;

import error.protocols.ErrorType;

public record MissingSemicolonError(int lineNumber) implements ErrorType {
    @Override
    public String categoryCode() {
        return "i";
    }

    @Override
    public String simpleErrorMessage() {
        return lineNumber + " " + categoryCode();
    }

    public String detailedErrorMessage() {
        return simpleErrorMessage();
    }
}
