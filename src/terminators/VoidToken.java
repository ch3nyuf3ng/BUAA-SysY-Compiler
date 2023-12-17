package terminators;

import foundation.Position;
import terminators.protocols.FuncTypeTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record VoidToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, FuncTypeTokenType {
    public VoidToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public VoidToken(Position beginningPosition, Position endingPosition) {
        this("void", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "VOIDTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return "VoidToken{" +
                "rawRepresentation='" + rawRepresentation + '\'' +
                ", beginningPosition=" + beginningPosition +
                ", endingPosition=" + endingPosition +
                '}';
    }
}
