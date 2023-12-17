package terminators;

import foundation.Position;
import terminators.protocols.EqualityTokenType;

import java.util.Objects;

public record NotEqualToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements EqualityTokenType {
    public NotEqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public NotEqualToken(Position beginningPosition, Position endingPosition) {
        this("!=", beginningPosition, endingPosition);
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

    @Override
    public String toString() {
        return "NotEqualToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
