package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record ElseToken(String rawRepresentation, Position position) implements TokenType {
    public ElseToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ElseToken(Position position) {
        this("else", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "ELSETK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
