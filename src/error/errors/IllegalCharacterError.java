package error.errors;

import error.errors.protocols.ErrorType;
import foundation.Position;
import foundation.StringUtils;

import java.util.Objects;

public record IllegalCharacterError(
        String line,
        Position beginningPosition,
        Position endingPosition
) implements ErrorType {
    public IllegalCharacterError {
        Objects.requireNonNull(line);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
        if (endingPosition.characterIndex() <= beginningPosition.characterIndex()) {
            throw new IllegalArgumentException(
                    "The ending position should be greater than the beginning position.\n"
                            + "beginning position: " + beginningPosition + '\n'
                            + "ending position:" + endingPosition + '\n'
            );
        }
    }

    @Override
    public String categoryCode() {
        return "a";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + beginningPosition().lineNumber();
    }

    @Override
    public String detailedErrorMessage() {
        final var count = endingPosition.characterIndex() - beginningPosition.characterIndex();
        final var whitespaces = " ".repeat(
                StringUtils.digitsCountOf(beginningPosition().lineNumber())
                        + 1
                        + StringUtils.digitsCountOf(beginningPosition().columnNumber())
        );
        return beginningPosition().lineNumber() + ' ' + line() + '\n'
                + whitespaces + "^".repeat(count) + '\n'
                + "Illegal Character.";
    }
}
