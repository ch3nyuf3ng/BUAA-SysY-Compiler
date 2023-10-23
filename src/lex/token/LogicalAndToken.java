package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LogicalAndToken(String rawRepresentation, Position position) implements TokenType {
    public LogicalAndToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LogicalAndToken(Position position) {
        this("&&", position);
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
        return rawRepresentation();
    }
}
