package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

public record Unknown(String rawRepresentation, Position position) implements NonTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "UNKNOWN";
    }

    @Override
    public String representation() {
        return rawRepresentation;
    }
}
