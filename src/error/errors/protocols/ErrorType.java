package error.errors.protocols;

public interface ErrorType {
    String categoryCode();
    String simpleErrorMessage();
    String detailedErrorMessage();
}
