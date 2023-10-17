package error;

import error.protocol.SimpleErrorType;
import foundation.Position;

public record IllegalCharacterError(
        Position position
) implements SimpleErrorType {
    @Override
    public String categoryCode() {
        return "a";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position.lineNumber();
    }
}
