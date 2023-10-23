package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record ReturnToken(String rawRepresentation, Position position) implements TokenType {
    public ReturnToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ReturnToken(Position position) {
        this("return", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RETURNTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
