package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record LogicalNotToken(
        Position position
) implements TokenType {
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
