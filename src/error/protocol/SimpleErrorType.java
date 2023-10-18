package error.protocol;

import foundation.Position;

@SuppressWarnings("SameReturnValue")
public interface SimpleErrorType {
    String categoryCode();
    Position position();
    String simpleErrorMessage();
}
