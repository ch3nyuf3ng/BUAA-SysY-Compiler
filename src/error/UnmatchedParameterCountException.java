package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class UnmatchedParameterCountException extends SimpleErrorABC {
    public UnmatchedParameterCountException(Position position) {
        super("d", position);
    }
}
