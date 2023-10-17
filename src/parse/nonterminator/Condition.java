package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class Condition implements NonTerminatorType {
    private final LogicalOrExpression logicalOrExpression;

    private Condition(LogicalOrExpression logicalOrExpression) {
        this.logicalOrExpression = logicalOrExpression;
    }

    public static Optional<Condition> parse(LexerType lexer) {
        Logger.info("Matching <Condition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalLogicOrExpression = LogicalOrExpression.parse(lexer);
            if (optionalLogicOrExpression.isEmpty()) break parse;
            final var logicalOrExpression = optionalLogicOrExpression.get();

            final var result = new Condition(logicalOrExpression);
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