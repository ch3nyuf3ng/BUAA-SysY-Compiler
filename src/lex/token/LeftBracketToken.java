package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LeftBracketToken(Position position) implements TokenType {
    public LeftBracketToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeftBracketToken that = (LeftBracketToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LBRACK";
    }

    @Override
    public String representation() {
        return "[";
    }
}
