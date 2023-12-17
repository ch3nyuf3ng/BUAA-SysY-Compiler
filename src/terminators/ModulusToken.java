package terminators;

import foundation.Position;
import terminators.protocols.MultiplicativeTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record ModulusToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, MultiplicativeTokenType {
    public ModulusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public ModulusToken(Position beginningPosition, Position endingPosition) {
        this("%", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "MOD";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "ModulusToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
