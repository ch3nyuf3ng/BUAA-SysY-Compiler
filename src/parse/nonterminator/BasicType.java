package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IntToken;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class BasicType implements NonTerminatorType {
    private final IntToken intToken;

    public BasicType(IntToken intToken) {
        this.intToken = Objects.requireNonNull(intToken);
    }

    public static Optional<BasicType> parse(LexerType lexer) {
        Logger.info("Matching <BasicType>");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var intToken = lexer.tryMatchAndConsumeTokenOf(IntToken.class);
            if (intToken.isEmpty()) break parse;

            final var result = new BasicType(intToken.get());
            Logger.info("Matched <BasicType>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <BasicType>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return intToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return intToken.representation();
    }

    @Override
    public String categoryCode() {
        return "<BType>";
    }

    @Override
    public TokenType lastTerminator() {
        return intToken;
    }
}
