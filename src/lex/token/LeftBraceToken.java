package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LeftBraceToken(Position position) implements TokenType {
    public LeftBraceToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeftBraceToken that = (LeftBraceToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "LBRACE";
    }

    @Override
    public String representation() {
        return "{";
    }
}
