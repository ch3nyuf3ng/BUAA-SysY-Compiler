package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

import java.util.Objects;

public record EqualToken(Position position) implements EqualityTokenType {
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
