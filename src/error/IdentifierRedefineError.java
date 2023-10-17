package error;

import error.protocol.SimpleErrorType;
import foundation.Position;
import lex.token.IdentifierToken;

public record IdentifierRedefineError(IdentifierToken identifierToken) implements SimpleErrorType {
    @Override
    public String categoryCode() {
        return "a";
    }

    @Override
    public Position position() {
        return identifierToken().position();
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position().lineNumber();
    }
}
