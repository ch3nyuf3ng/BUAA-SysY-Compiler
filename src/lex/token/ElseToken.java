package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class ElseToken implements TokenType {
    private final Position position;

    public ElseToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElseToken elseToken = (ElseToken) o;
        return Objects.equals(position, elseToken.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
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
        return "ELSETK";
    }

    @Override
    public String representation() {
        return "else";
    }
}
