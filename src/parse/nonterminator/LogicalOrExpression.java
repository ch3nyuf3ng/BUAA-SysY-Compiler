package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LogicalOrToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogicalOrExpression implements NonTerminatorType {
    private final LogicalAndExpression firstLogicalAndExpression;
    private final List<Pair<LogicalOrToken, LogicalAndExpression>> operatorWithExpressionList;

    private LogicalOrExpression(
            LogicalAndExpression firstLogicalAndExpression,
            List<Pair<LogicalOrToken, LogicalAndExpression>> operatorWithExpressionList
    ) {
        this.firstLogicalAndExpression = firstLogicalAndExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<LogicalOrExpression> parse(LexerType lexer) {
        Logger.info("Matching <LogicalOrExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstLogicalAndExpression = LogicalAndExpression.parse(lexer);
            if (firstLogicalAndExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalOrToken, LogicalAndExpression>>();
            while (lexer.isMatchedTokenOf(LogicalOrToken.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(LogicalOrToken.class);
                if (operator.isEmpty()) break;

                final var additionalLogicalAndExpression = LogicalAndExpression.parse(lexer);
                if (additionalLogicalAndExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalLogicalAndExpression.get()));
            }

            final var result = new LogicalOrExpression(firstLogicalAndExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <LogicalOrExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <LogicalOrExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstLogicalAndExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstLogicalAndExpression.detailedRepresentation()).append(categoryCode()).append('\n');
        operatorWithExpressionList.forEach(i -> stringBuilder.append(i.first().detailedRepresentation()).append(i.second()
                .detailedRepresentation()).append(categoryCode()).append('\n'));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstLogicalAndExpression,
                operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<LOrExp>";
    }
}
