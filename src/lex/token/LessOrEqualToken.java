package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record LessOrEqualToken(Position position) implements TokenType, RelaitionalOperatorTokenType {
    public LessOrEqualToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessOrEqualToken that = (LessOrEqualToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "LEQ";
    }

    @Override
    public String representation() {
        return "<=";
    }
}
