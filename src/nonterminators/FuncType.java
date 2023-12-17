package nonterminators;

import nonterminators.protocols.NonTerminatorType;
import terminators.protocols.FuncTypeTokenType;
import terminators.protocols.TokenType;

public record FuncType(
        FuncTypeTokenType funcType
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return funcType;
    }

    @Override
    public String detailedRepresentation() {
        return funcType.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return funcType.representation();
    }

    @Override
    public String categoryCode() {
        return "<FuncType>";
    }

    @Override
    public String toString() {
        return "FuncType{" +
                "funcType=" + funcType +
                '}';
    }
}
