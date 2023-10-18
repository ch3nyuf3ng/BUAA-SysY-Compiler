package lex.token;

import foundation.Position;
import lex.protocol.BasicTypeTokenType;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

public record IntToken(Position position) implements TokenType, FuncTypeTokenType, BasicTypeTokenType {
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
