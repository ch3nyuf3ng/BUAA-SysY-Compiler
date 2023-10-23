package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record RightBraceToken(String rawRepresentation, Position position) implements TokenType {
    public RightBraceToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public RightBraceToken(Position position) {
        this("}", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "RBRACE";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
