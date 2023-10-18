package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

import java.util.Objects;

public class EqualToken implements EqualityTokenType {
    private final Position position;

    public EqualToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EqualToken that = (EqualToken) o;
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
        return "EQL";
    }

    @Override
    public String representation() {
        return "==";
    }
}
