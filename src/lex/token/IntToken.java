package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record IntToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "INTTK";
    }

    @Override
    public String representation() {
        return "int";
    }
}
