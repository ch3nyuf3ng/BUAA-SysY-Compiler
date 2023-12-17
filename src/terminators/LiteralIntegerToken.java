package terminators;

import foundation.Position;
import terminators.protocols.NumberTokenType;

import java.util.Objects;

public record LiteralIntegerToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements NumberTokenType {
    public LiteralIntegerToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "INTCON";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "LiteralIntegerToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }

    public int toInt() {
        return Integer.parseInt(rawRepresentation);
    }
}
