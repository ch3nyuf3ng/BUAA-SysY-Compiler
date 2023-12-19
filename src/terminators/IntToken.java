package terminators;

import foundation.Position;
import terminators.protocols.BasicTypeTokenType;
import terminators.protocols.FuncTypeTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;

public record IntToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements TokenType, FuncTypeTokenType, BasicTypeTokenType {
    public IntToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    public IntToken(Position beginningPosition, Position endingPosition) {
        this("int", beginningPosition, endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "INTTK";
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
