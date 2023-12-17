package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record LeftParenthesisToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public LeftParenthesisToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public LeftParenthesisToken(Position beginningPosition, Position endingPosition) {
        this("(", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "LPARENT";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "LeftParenthesisToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
