package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record PrintfToken(Position position) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "PRINTFTK";
    }

    @Override
    public String representation() {
        return "printf";
    }
}
