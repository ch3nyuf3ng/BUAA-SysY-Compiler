package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class RightBracketMissingException extends SimpleErrorABC {
    public RightBracketMissingException(Position position) {
        super("k", position);
    }
}
