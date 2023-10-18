package lex.token;

import foundation.Position;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

public record LogicalNotToken(Position position) implements TokenType, UnaryOperatorTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NOT";
    }

    @Override
    public String representation() {
        return "!";
    }
}
