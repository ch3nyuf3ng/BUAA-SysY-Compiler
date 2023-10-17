package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record LiteralIntegerToken(
        String rawRepresentation,
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "INTCON";
    }

    @Override
    public String representation() {
        return rawRepresentation;
    }

    public int value() {
        return Integer.parseInt(rawRepresentation);
    }
}
