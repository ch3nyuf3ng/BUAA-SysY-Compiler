package terminators;

import foundation.Position;
import terminators.protocols.AdditiveTokenType;
import terminators.protocols.TokenType;
import terminators.protocols.UnaryOperatorTokenType;

import java.util.Objects;

public record PlusToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    public PlusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public PlusToken(Position beginningPosition, Position endingPosition) {
        this("+", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "PLUS";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "PlusToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
