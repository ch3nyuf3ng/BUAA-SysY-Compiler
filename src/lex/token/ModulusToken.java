package lex.token;

import foundation.Position;
import lex.protocol.MultiplicativeTokenType;
import lex.protocol.TokenType;

public record ModulusToken(Position position) implements TokenType, MultiplicativeTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "MOD";
    }

    @Override
    public String representation() {
        return "%";
    }
}
