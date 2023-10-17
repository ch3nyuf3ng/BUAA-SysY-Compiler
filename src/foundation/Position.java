package foundation;

public record Position(
        int characterIndex,
        int lineNumber,
        int columnNumber
) {}