package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record ContinueToken(String rawRepresentation, Position position) implements TokenType {
    public ContinueToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ContinueToken(Position position) {
        this("continue", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "CONTINUETK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
