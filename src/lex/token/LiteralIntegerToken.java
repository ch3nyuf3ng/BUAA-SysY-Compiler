package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LiteralIntegerToken(String rawRepresentation, Position position) implements TokenType {
    public LiteralIntegerToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

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
        return rawRepresentation();
    }

}
