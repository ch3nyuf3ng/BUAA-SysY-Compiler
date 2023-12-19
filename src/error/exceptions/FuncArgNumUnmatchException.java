package error.exceptions;

import error.protocols.ErrorType;

public class FuncArgNumUnmatchException extends Exception implements ErrorType {
    private final int lineNumber;

    public FuncArgNumUnmatchException(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public String categoryCode() {
        return "d";
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
