package error;

import error.protocol.SimpleErrorType;
import foundation.Position;

public record SemicolonMissingError(
        Position position
) implements SimpleErrorType {
    @Override
    public String categoryCode() {
        return "i";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position.lineNumber();
    }
}