package lex.token;

import foundation.Position;
import lex.protocol.AdditiveTokenType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

public record MinusToken(Position position) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MINU";
    }

    @Override
    public String representation() {
        return "-";
    }
}
