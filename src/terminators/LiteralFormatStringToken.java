package terminators;

import foundation.Position;
import terminators.protocols.TokenType;

import java.util.Objects;

public record LiteralFormatStringToken(
        String content,
        Position beginningPosition,
        Position endingPosition
) implements TokenType {
    static public boolean isLegalCharacter(char character) {
        final var ascii = (int) character;
        return ascii == 32 || ascii == 33 || 40 <= ascii && ascii <= 126;
    }

    public LiteralFormatStringToken {
        Objects.requireNonNull(content);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
    }

    @Override
    public String rawRepresentation() {
        return content();
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "STRCON";
    }

    @Override
    public String representation() {
        return "\"" + content() + "\"";
    }

    @Override
    public String toString() {
        return representation();
    }
}