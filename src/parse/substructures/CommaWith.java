package parse.substructures;

import lex.token.CommaToken;

public record CommaWith<T> (
        CommaToken commaToken,
        T entity
) {
}