package terminators;

import foundation.Position;
import terminators.protocols.MultiplicativeTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record MultiplyToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, MultiplicativeTokenType {
    public MultiplyToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public MultiplyToken(Position beginningPosition, Position endingPosition) {
        this("*", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MULT";
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
