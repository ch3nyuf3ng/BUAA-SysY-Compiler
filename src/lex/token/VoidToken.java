package lex.token;

import foundation.Position;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public class VoidToken implements TokenType, FuncTypeTokenType {
    private final Position position;

    public VoidToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoidToken voidToken = (VoidToken) o;
        return Objects.equals(position, voidToken.position);
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
        return "VOIDTK";
    }

    @Override
    public String representation() {
        return "void";
    }
}
