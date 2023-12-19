package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record ReturnToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public ReturnToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public ReturnToken(Position beginningPosition, Position endingPosition) {
        this("return", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RETURNTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return representation();
    }
}
