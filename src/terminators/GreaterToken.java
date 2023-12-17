package terminators;

import foundation.Position;
import terminators.protocols.RelaitionalOperatorTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record GreaterToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, RelaitionalOperatorTokenType {
    public GreaterToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public GreaterToken(Position beginningPosition, Position endingPosition) {
        this(">", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "GRE";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "GreaterToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
