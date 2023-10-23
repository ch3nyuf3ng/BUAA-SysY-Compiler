package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LogicalOrToken(String rawRepresentation, Position position) implements TokenType {
    public LogicalOrToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LogicalOrToken(Position position) {
        this("||", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "OR";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
