package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record LogicalOrToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "OR";
    }

    @Override
    public String representation() {
        return "||";
    }
}
