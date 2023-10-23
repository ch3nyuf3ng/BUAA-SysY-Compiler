package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record AssignToken(String rawRepresentation, Position position) implements TokenType {
    public AssignToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public AssignToken(Position position) {
        this("=", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "ASSIGN";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
