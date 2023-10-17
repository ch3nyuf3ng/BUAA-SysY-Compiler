package error.abc;

import error.protocol.SimpleErrorType;
import foundation.Position;

abstract public class SimpleErrorABC implements SimpleErrorType {
    private final String categoryCode;
    private final Position position;

    public SimpleErrorABC(String categoryCode, Position position) {
        this.categoryCode = categoryCode;
        this.position = position;
    }

    @Override
    public String categoryCode() {
        return categoryCode;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode + " " + position.lineNumber();
    }
}
