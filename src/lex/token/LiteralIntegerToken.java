package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LiteralIntegerToken(String rawRepresentation, Position position) implements TokenType {
    public LiteralIntegerToken(String rawRepresentation, Position position) {
        this.rawRepresentation = Objects.requireNonNull(rawRepresentation);
        this.position = Objects.requireNonNull(position);
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
        return rawRepresentation;
    }

}
