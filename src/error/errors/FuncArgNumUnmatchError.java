package error.errors;

import error.errors.protocols.ErrorType;

public record FuncArgNumUnmatchError(
        int lineNumber
) implements ErrorType {
    @Override
    public String categoryCode() {
        return "d";
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
