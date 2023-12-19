package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record ElseToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public ElseToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public ElseToken(Position beginningPosition, Position endingPosition) {
        this("else", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "ELSETK";
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
