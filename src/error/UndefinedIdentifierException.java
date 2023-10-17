package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class UndefinedIdentifierException extends SimpleErrorABC {
    public UndefinedIdentifierException(Position position) {
        super("c", position);
    }
}
