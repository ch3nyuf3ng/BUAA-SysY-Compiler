package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record ElseToken(Position position) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "ELSETK";
    }

    @Override
    public String representation() {
        return "else";
    }
}
