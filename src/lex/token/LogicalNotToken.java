package lex.token;

import foundation.Position;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

import java.util.Objects;

public record LogicalNotToken(Position position) implements TokenType, UnaryOperatorTokenType {
    public LogicalNotToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicalNotToken that = (LogicalNotToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NOT";
    }

    @Override
    public String representation() {
        return "!";
    }
}
