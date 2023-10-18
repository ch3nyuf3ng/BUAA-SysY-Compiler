package foundation;

import java.util.Objects;

public class Position {
    private final int characterIndex;
    private final int lineNumber;
    private final int columnNumber;

    public Position(int characterIndex, int lineNumber, int columnNumber) {
        this.characterIndex = characterIndex;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int characterIndex() {
        return characterIndex;
    }

    public int lineNumber() {
        return lineNumber;
    }

    public int columnNumber() {
        return columnNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return characterIndex == position.characterIndex && lineNumber == position.lineNumber
                && columnNumber == position.columnNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterIndex, lineNumber, columnNumber);
    }
}