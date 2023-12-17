package terminators;

import foundation.Position;
import terminators.protocols.MultiplicativeTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record DivideToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, MultiplicativeTokenType {
    public DivideToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public DivideToken(Position beginningPosition, Position endingPosition) {
        this("/", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "DIV";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "DivideToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
