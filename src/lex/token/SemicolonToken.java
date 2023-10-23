package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record SemicolonToken(String rawRepresentation, Position position) implements TokenType {
    public SemicolonToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public SemicolonToken(Position position) {
        this(";", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "SEMICN";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
