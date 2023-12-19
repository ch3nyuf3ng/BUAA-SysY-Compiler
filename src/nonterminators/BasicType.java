package nonterminators;

import foundation.protocols.EvaluationType;
import foundation.typing.IntType;
import nonterminators.protocols.NonTerminatorType;
import terminators.IntToken;
import terminators.protocols.BasicTypeTokenType;
import terminators.protocols.TokenType;

public record BasicType(BasicTypeTokenType token) implements NonTerminatorType {
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
    public String toString() {
        return token.representation();
    }

    public EvaluationType evaluationType() {
        if (token instanceof IntToken) {
            return new IntType();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
