package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record LeftBracketToken(
        String rawRepresentation, Position position
) implements TokenType {
    public LeftBracketToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LeftBracketToken(Position position) {
        this("[", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LBRACK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
