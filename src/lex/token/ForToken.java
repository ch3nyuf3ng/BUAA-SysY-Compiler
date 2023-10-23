package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record ForToken(String rawRepresentation, Position position) implements TokenType {
    public ForToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ForToken(Position position) {
        this("for", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "FORTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
