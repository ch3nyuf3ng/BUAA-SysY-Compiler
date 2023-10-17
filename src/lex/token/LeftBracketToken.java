package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record LeftBracketToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LBRACK";
    }

    @Override
    public String representation() {
        return "[";
    }
}
