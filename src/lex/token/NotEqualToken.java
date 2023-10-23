package lex.token;

import foundation.Position;
import lex.protocol.EqualityTokenType;

import java.util.Objects;

public record NotEqualToken(
        String rawRepresentation, Position position
) implements EqualityTokenType {
    public NotEqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public NotEqualToken(Position position) {
        this("!=", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NEQ";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
