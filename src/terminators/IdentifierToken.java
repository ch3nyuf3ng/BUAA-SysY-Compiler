package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record IdentifierToken(
        String identifier,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    public IdentifierToken {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    @Override
    public String rawRepresentation() {
        return identifier();
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "IDENFR";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }

    @Override
    public String toString() {
        return identifier;
    }

    public int lineNumber() {
        return beginningPosition.lineNumber();
    }
}