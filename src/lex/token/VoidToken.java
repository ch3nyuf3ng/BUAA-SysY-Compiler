package lex.token;

import foundation.Position;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

public record VoidToken(Position position) implements TokenType, FuncTypeTokenType {
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
