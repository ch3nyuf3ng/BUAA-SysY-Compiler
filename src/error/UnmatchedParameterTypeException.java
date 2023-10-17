package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class UnmatchedParameterTypeException extends SimpleErrorABC {
    public UnmatchedParameterTypeException(Position position) {
        super("e", position);
    }
}
