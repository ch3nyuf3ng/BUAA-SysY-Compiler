package error.protocol;

import foundation.Position;

public interface SimpleErrorType {
    String categoryCode();
    Position position();
    String simpleErrorMessage();
}
