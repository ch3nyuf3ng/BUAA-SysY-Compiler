package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class RightBraceToken implements TokenType {
    private final Position position;

    public RightBraceToken(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RightBraceToken that = (RightBraceToken) o;
        return Objects.equals(position, that.position);
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
        return "RBRACE";
    }

    @Override
    public String representation() {
        return "}";
    }
}
