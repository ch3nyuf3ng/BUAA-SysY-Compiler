package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record LeftParenthesisToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LPARENT";
    }

    @Override
    public String representation() {
        return "(";
    }
}