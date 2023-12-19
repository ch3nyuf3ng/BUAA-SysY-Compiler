package error.protocols;

public interface ErrorType {
    int lineNumber();
    String categoryCode();
    String simpleErrorMessage();
    String detailedErrorMessage();
}
