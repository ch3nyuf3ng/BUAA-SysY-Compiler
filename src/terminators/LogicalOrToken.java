package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record LogicalOrToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public LogicalOrToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public LogicalOrToken(Position beginningPosition, Position endingPosition) {
        this("||", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "OR";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "LogicalOrToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
