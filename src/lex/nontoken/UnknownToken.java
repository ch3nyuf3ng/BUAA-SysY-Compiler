package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

import java.util.Objects;

public record UnknownToken(String rawRepresentation, Position position) implements NonTokenType {
    public UnknownToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

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
