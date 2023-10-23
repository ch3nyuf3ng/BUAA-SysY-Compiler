package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record GetIntToken(String rawRepresentation, Position position) implements TokenType {
    public GetIntToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public GetIntToken(Position position) {
        this("getint", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "GETINTTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
