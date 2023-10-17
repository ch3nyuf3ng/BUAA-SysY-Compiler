package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class RightParenthesisMissingException extends SimpleErrorABC {
    public RightParenthesisMissingException(Position position) {
        super("j", position);
    }
}
