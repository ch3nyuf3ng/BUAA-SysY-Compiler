package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record IfToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "IFTK";
    }

    @Override
    public String representation() {
        return "if";
    }
}
