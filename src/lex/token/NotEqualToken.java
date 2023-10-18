package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

import java.util.Objects;

public record NotEqualToken(Position position) implements EqualityTokenType {
    public NotEqualToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotEqualToken that = (NotEqualToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NEQ";
    }

    @Override
    public String representation() {
        return "!=";
    }
}
