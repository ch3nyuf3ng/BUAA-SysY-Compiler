package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record MainToken(String rawRepresentation, Position position) implements TokenType {
    public MainToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public MainToken(Position position) {
        this("main", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MAINTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
