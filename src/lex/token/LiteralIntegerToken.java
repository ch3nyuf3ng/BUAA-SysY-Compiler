package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class LiteralIntegerToken implements TokenType {
    private final String rawRepresentation;
    private final Position position;

    public LiteralIntegerToken(String rawRepresentation, Position position) {
        this.rawRepresentation = Objects.requireNonNull(rawRepresentation);
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralIntegerToken that = (LiteralIntegerToken) o;
        return Objects.equals(rawRepresentation, that.rawRepresentation) && Objects.equals(
                position,
                that.position
        );
    }

    public String rawRepresentation() {
        return rawRepresentation;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawRepresentation, position);
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

    public int value() {
        return Integer.parseInt(rawRepresentation);
    }
}
