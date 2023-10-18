package error;

import error.protocol.SimpleErrorType;
import foundation.Position;

import java.util.Objects;

public class SemicolonMissingError implements SimpleErrorType {
    private final Position position;

    public SemicolonMissingError(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemicolonMissingError that = (SemicolonMissingError) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public String categoryCode() {
        return "i";
    }

    @Override
    public String simpleErrorMessage() {
        return categoryCode() + " " + position.lineNumber();
    }
}