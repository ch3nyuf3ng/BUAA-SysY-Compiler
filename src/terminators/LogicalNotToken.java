package terminators;

import foundation.Position;
import terminators.protocols.TokenType;
import terminators.protocols.UnaryOperatorTokenType;

import java.util.Objects;

public record LogicalNotToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, UnaryOperatorTokenType {
    public LogicalNotToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public LogicalNotToken(Position beginningPosition, Position endingPosition) {
        this("!", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NOT";
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
