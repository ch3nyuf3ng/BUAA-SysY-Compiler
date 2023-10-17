package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

public record GreaterToken(
        Position position
) implements TokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "GRE";
    }

    @Override
    public String representation() {
        return ">";
    }
}
