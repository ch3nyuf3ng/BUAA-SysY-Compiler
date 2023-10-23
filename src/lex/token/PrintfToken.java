package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record PrintfToken(String rawRepresentation, Position position) implements TokenType {
    public PrintfToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public PrintfToken(Position position) {
        this("printf", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "PRINTFTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
