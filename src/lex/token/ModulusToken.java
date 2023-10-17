package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record ModulusToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "MOD";
    }

    @Override
    public String representation() {
        return "%";
    }
}
