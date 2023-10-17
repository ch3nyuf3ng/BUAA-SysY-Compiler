package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class UnmatchedFormatStringArgumentsCountException extends SimpleErrorABC {
    public UnmatchedFormatStringArgumentsCountException(Position position) {
        super("l", position);
    }
}
