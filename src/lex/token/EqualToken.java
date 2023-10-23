package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

import java.util.Objects;

public record EqualToken(String rawRepresentation, Position position) implements EqualityTokenType {
    public EqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public EqualToken(Position position) {
        this("==", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "EQL";
    }

    @Override
    public String representation() {
        return "==";
    }
}
