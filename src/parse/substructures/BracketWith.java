package parse.substructures;

import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;

import java.util.Objects;
import java.util.Optional;

public class BracketWith<T> {
    private final LeftBracketToken leftBracketToken;
    private final T entity;
    private final Optional<RightBracketToken> rightBracketToken;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BracketWith<?> that = (BracketWith<?>) o;
        return Objects.equals(leftBracketToken, that.leftBracketToken)
                && Objects.equals(entity, that.entity)
                && Objects.equals(rightBracketToken, that.rightBracketToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftBracketToken, entity, rightBracketToken);
    }

    public LeftBracketToken leftBracketToken() {
        return leftBracketToken;
    }

    public T entity() {
        return entity;
    }

    public Optional<RightBracketToken> rightBracketToken() {
        return rightBracketToken;
    }
}
