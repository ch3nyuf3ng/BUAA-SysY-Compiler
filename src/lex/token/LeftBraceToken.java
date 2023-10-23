package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LeftBraceToken(
        String rawRepresentation, Position position
) implements TokenType {
    public LeftBraceToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LeftBraceToken(Position position) {
        this("{", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "LBRACE";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
