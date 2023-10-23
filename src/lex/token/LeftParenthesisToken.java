package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LeftParenthesisToken(String rawRepresentation, Position position) implements TokenType {
    public LeftParenthesisToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LeftParenthesisToken(Position position) {
        this("(", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LPARENT";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
