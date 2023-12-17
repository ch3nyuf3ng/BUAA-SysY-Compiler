package foundation;

import terminators.LeftBracketToken;
import terminators.RightBracketToken;

import java.util.Objects;
import java.util.Optional;

public record BracketWith<T>(
        LeftBracketToken leftBracketToken,
        T entity,
        Optional<RightBracketToken> rightBracketToken
) {
    public BracketWith(
            LeftBracketToken leftBracketToken,
            T entity,
            Optional<RightBracketToken> rightBracketToken
    ) {
        this.leftBracketToken = Objects.requireNonNull(leftBracketToken);
        this.entity = Objects.requireNonNull(entity);
        this.rightBracketToken = Objects.requireNonNull(rightBracketToken);
    }

    @Override
    public String toString() {
        return "[" + entity + "]";
    }
}
