package error;

import error.protocol.SimpleErrorType;
import foundation.Position;

public record UseContinueInNonRepeatableBlockException(Position position) implements SimpleErrorType {
    @Override
    public String categoryCode() {
        return "m";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position.lineNumber();
    }
}
