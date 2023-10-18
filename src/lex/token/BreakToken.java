package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record BreakToken(Position position) implements TokenType {
    public BreakToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "BREAKTK";
    }

    @Override
    public String representation() {
        return "break";
    }
}
