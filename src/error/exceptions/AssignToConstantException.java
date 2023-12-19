package error.exceptions;

import error.protocols.ErrorType;

public class AssignToConstantException extends Exception implements ErrorType {
    private final int lineNumber;

    public AssignToConstantException(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String categoryCode() {
        return "h";
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
