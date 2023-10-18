package error;

import error.protocol.SimpleErrorType;
import foundation.Position;

import java.util.Objects;

public record IllegalCharacterError(Position position) implements SimpleErrorType {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IllegalCharacterError that = (IllegalCharacterError) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public String categoryCode() {
        return "a";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position.lineNumber();
    }
}
