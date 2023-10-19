package lex.protocol;

import foundation.Position;

import java.util.Optional;

public interface LexerType {
    Optional<TokenType> currentToken();

    Position beginningPosition();

    void resetPosition(Position position);

    <T> Optional<T> tryMatchAndConsumeTokenOf(Class<T> targetTokenClass);

    <T> boolean isMatchedTokenOf(Class<T> targetTokenClass);
}
