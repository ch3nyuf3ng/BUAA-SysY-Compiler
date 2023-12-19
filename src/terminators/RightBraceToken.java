package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record RightBraceToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public RightBraceToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public RightBraceToken(Position beginningPosition, Position endingPosition) {
        this("}", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "RBRACE";
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
