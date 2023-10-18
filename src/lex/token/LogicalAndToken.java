package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LogicalAndToken(Position position) implements TokenType {
    public LogicalAndToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicalAndToken that = (LogicalAndToken) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "AND";
    }

    @Override
    public String representation() {
        return "&&";
    }
}
