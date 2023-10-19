package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class Condition implements NonTerminatorType {
    private final LogicalOrExpression logicalOrExpression;

    public Condition(LogicalOrExpression logicalOrExpression) {
        this.logicalOrExpression = Objects.requireNonNull(logicalOrExpression);
    }

    public static Optional<Condition> parse(LexerType lexer) {
        Logger.info("Matching <Condition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var logicalOrExpression = LogicalOrExpression.parse(lexer);
            if (logicalOrExpression.isEmpty()) break parse;

            final var result = new Condition(logicalOrExpression.get());
            Logger.info("Matched <Condition>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <Condition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return logicalOrExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return logicalOrExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<Cond>";
    }

    @Override
    public TokenType lastTerminator() {
        return null;
    }
}
