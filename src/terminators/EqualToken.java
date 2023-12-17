package terminators;

import foundation.Position;
import terminators.protocols.EqualityTokenType;

import java.util.Objects;

public record EqualToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements EqualityTokenType {
    public EqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public EqualToken(Position beginningPosition, Position endingPosition) {
        this("==", beginningPosition, endingPosition);
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

    @Override
    public String toString() {
        return "EqualToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
