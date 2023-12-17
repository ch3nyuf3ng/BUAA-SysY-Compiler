package terminators.nontokens;

import foundation.Position;
import terminators.protocols.NonTokenType;

import java.util.Objects;

public record UnknownToken(
        String rawRepresentation,
        Position beginningPosition,
        Position endingPosition
) implements NonTokenType {
    public UnknownToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "UNKNOWN";
    }

    @Override
    public String representation() {
        return rawRepresentation;
    }
}
