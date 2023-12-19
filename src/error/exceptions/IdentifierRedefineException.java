package error.exceptions;

import error.protocols.ErrorType;

public class IdentifierRedefineException extends Exception implements ErrorType {
    private final int lineNumber;

    public IdentifierRedefineException(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String categoryCode() {
        return "b";
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
