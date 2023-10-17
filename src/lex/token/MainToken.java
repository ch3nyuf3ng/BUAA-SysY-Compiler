package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record MainToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MAINTK";
    }

    @Override
    public String representation() {
        return "main";
    }
}
