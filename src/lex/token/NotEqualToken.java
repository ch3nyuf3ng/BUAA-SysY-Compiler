package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record NotEqualToken(
        Position position
) implements TokenType {
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
