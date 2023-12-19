package nonterminators;

import nonterminators.protocols.NonTerminatorType;
import terminators.protocols.TokenType;
import terminators.protocols.UnaryOperatorTokenType;

public record UnaryOperator(
        UnaryOperatorTokenType operator
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return operator.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return operator.representation();
    }

    @Override
    public String categoryCode() {
        return "<UnaryOp>";
    }

    @Override
    public TokenType lastTerminator() {
        return operator;
    }

    @Override
    public String toString() {
        return representation();
    }
}
