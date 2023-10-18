package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record GreaterOrEqualToken(Position position) implements TokenType, RelaitionalOperatorTokenType {
    public GreaterOrEqualToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GreaterOrEqualToken that = (GreaterOrEqualToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "GEQ";
    }

    @Override
    public String representation() {
        return ">=";
    }
}
