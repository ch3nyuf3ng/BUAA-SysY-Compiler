package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record ConstToken(String rawRepresentation, Position position) implements TokenType {
    public ConstToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ConstToken(Position position) {
        this("const", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "CONSTTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
