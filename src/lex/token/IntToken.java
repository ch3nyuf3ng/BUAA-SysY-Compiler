package lex.token;

import foundation.Position;
import lex.protocol.BasicTypeTokenType;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public class IntToken implements TokenType, FuncTypeTokenType, BasicTypeTokenType {
    private final Position position;

    public IntToken(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntToken intToken = (IntToken) o;
        return Objects.equals(position, intToken.position);
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
        return "INTTK";
    }

    @Override
    public String representation() {
        return "int";
    }
}
