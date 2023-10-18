package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class BreakToken implements TokenType {
    private final Position position;

    public BreakToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public Position position() {
        return position;
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
