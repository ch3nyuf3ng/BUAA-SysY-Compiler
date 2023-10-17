package lex.protocol;

import foundation.Position;

import java.util.Optional;

public interface LexerType {
    Optional<TokenType> currentToken();
    void consumeToken();
    void resetPosition(Position position);
    Position beginningPosition();
    Position currentPosition();

    <T> Optional<T> getAndConsumeTokenOf(Class<T> targetClass);
}
