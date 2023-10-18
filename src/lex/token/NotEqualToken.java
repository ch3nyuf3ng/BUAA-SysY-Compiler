package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

public record NotEqualToken(Position position) implements EqualityTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NEQ";
    }

    @Override
    public String representation() {
        return "!=";
    }
}
