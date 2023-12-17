package nonterminators;

import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.WithSizeType;
import terminators.IntToken;
import terminators.protocols.BasicTypeTokenType;
import terminators.protocols.TokenType;

public record BasicType(
        BasicTypeTokenType token
) implements NonTerminatorType, WithSizeType {
    @Override
    public String detailedRepresentation() {
        return token.detailedRepresentation();
    }

    @Override
    public String representation() {
        return token.representation();
    }

    @Override
    public String categoryCode() {
        return "<BType>";
    }

    @Override
    public TokenType lastTerminator() {
        return token;
    }

    @Override
    public int size() {
        if (token instanceof IntToken) {
            return 1;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return token.representation();
    }

    public boolean hasSameTypeWith(BasicType other) {
        return token.getClass().equals(other.token.getClass());
    }
}
