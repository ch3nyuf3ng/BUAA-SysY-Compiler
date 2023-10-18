package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record RightParenthesisToken(Position position) implements TokenType {
    public RightParenthesisToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RightParenthesisToken that = (RightParenthesisToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RPARENT";
    }

    @Override
    public String representation() {
        return ")";
    }
}
