package error;

import error.protocol.SimpleErrorType;
import foundation.Position;
import lex.token.IdentifierToken;

import java.util.Objects;

public class IdentifierRedefineError implements SimpleErrorType {
    private final IdentifierToken identifierToken;

    public IdentifierRedefineError(IdentifierToken identifierToken) {
        this.identifierToken = Objects.requireNonNull(identifierToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierRedefineError that = (IdentifierRedefineError) o;
        return Objects.equals(identifierToken, that.identifierToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierToken);
    }

    public IdentifierToken identifierToken() {
        return identifierToken;
    }

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
