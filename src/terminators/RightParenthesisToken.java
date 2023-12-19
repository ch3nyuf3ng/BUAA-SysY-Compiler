package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record RightParenthesisToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public RightParenthesisToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public RightParenthesisToken(Position beginningPosition, Position endingPosition) {
        this(")", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "RPARENT";
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
