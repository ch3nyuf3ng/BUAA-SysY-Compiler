package error;

import error.abc.SimpleErrorABC;
import foundation.Position;

public class ReturnValueInProcedureException extends SimpleErrorABC {
    public ReturnValueInProcedureException(Position position) {
        super("f", position);
    }
}
