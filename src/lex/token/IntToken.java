package lex.token;

import foundation.Position;
import lex.protocol.BasicTypeTokenType;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record IntToken(Position position) implements TokenType, FuncTypeTokenType, BasicTypeTokenType {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntToken intToken = (IntToken) o;
        return Objects.equals(position, intToken.position);
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
