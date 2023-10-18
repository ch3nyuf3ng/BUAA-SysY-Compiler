package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LogicalAndToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.*;

public class LogicalAndExpression implements NonTerminatorType {
    private final EqualityExpression firstEqualityExpression;
    private final List<Pair<LogicalAndToken, EqualityExpression>> operatorWithExpressionList;

    public LogicalAndExpression(
            EqualityExpression firstEqualityExpression,
            List<Pair<LogicalAndToken, EqualityExpression>> operatorWithExpressionList
    ) {
        this.firstEqualityExpression = Objects.requireNonNull(firstEqualityExpression);
        this.operatorWithExpressionList = Collections.unmodifiableList(operatorWithExpressionList);
    }

    public static Optional<LogicalAndExpression> parse(LexerType lexer) {
        Logger.info("Matching <LogicalAndExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstEqualityExpression = EqualityExpression.parse(lexer);
            if (firstEqualityExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalAndToken, EqualityExpression>>();
            while (lexer.isMatchedTokenOf(LogicalAndToken.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(LogicalAndToken.class);
                if (operator.isEmpty()) break;

                final var additionalEqualityExpression = EqualityExpression.parse(lexer);
                if (additionalEqualityExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalEqualityExpression.get()));
            }

            final var result = new LogicalAndExpression(firstEqualityExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <LogicalAndExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <LogicalAndExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstEqualityExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstEqualityExpression.detailedRepresentation()).append(categoryCode()).append('\n');
        operatorWithExpressionList.forEach(i -> stringBuilder.append(i.first().detailedRepresentation()).append(i.second()
                .detailedRepresentation()).append(categoryCode()).append('\n'));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstEqualityExpression,
                operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<LAndExp>";
    }
}
