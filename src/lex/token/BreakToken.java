package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record BreakToken(String rawRepresentation, Position position) implements TokenType {
    public BreakToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public BreakToken(Position position) {
        this("break", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "BREAKTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
