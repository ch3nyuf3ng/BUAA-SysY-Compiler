package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record RightParenthesisToken(String rawRepresentation, Position position) implements TokenType {
    public RightParenthesisToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public RightParenthesisToken(Position position) {
        this(")", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RPARENT";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
