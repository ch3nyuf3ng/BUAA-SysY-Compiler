package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class ForToken implements TokenType {
    private final Position position;

    public ForToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForToken forToken = (ForToken) o;
        return Objects.equals(position, forToken.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "FORTK";
    }

    @Override
    public String representation() {
        return "for";
    }
}
