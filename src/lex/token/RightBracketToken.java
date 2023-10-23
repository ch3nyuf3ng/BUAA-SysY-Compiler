package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record RightBracketToken(String rawRepresentation, Position position) implements TokenType {
    public RightBracketToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public RightBracketToken(Position position) {
        this("]", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RBRACK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
