package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class ReturnValueMissingInFunctionException extends SimpleErrorABC {
    public ReturnValueMissingInFunctionException(Position position) {
        super("g", position);
    }
}
