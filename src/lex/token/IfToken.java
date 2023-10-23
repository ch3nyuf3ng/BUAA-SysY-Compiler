package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record IfToken(String rawRepresentation, Position position) implements TokenType {
    public IfToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public IfToken(Position position) {
        this("if", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "IFTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
