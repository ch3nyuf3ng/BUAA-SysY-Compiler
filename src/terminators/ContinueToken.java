package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record ContinueToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public ContinueToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public ContinueToken(Position beginningPosition, Position endingPosition) {
        this("continue", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "CONTINUETK";
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
