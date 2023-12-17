package terminators;

import foundation.Position;
import terminators.protocols.AdditiveTokenType;
import terminators.protocols.TokenType;
import terminators.protocols.UnaryOperatorTokenType;

import java.util.Objects;

public record MinusToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    public MinusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public MinusToken(Position beginningPosition, Position endingPosition) {
        this("-", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MINU";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "MinusToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
