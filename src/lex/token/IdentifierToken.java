package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record IdentifierToken(
        String name,
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "IDENFR";
    }

    @Override
    public String representation() {
        return name;
    }
}