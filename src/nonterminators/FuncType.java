package nonterminators;

import foundation.protocols.EvaluationType;
import foundation.typing.IntType;
import foundation.typing.VoidType;
import nonterminators.protocols.NonTerminatorType;
import terminators.IntToken;
import terminators.VoidToken;
import terminators.protocols.FuncTypeTokenType;
import terminators.protocols.TokenType;

public record FuncType(FuncTypeTokenType funcTypeToken) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return funcTypeToken;
    }

    @Override
    public String detailedRepresentation() {
        return funcTypeToken.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return funcTypeToken.representation();
    }

    @Override
    public String categoryCode() {
        return "<FuncType>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public boolean isVoid() {
        return funcTypeToken instanceof VoidToken;
    }

    public EvaluationType evaluationType() {
        if (funcTypeToken instanceof IntToken) {
            return new IntType();
        } else if (funcTypeToken instanceof VoidToken) {
            return new VoidType();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
