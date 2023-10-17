package parse.nonterminator;

import foundation.Pair;
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
            final var optionalLogicalAndExpression = LogicalAndExpression.parse(lexer);
            if (optionalLogicalAndExpression.isEmpty()) break parse;
            final var firstLogicalAndExpression = optionalLogicalAndExpression.get();

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalOrToken, LogicalAndExpression>>();
            while (true) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof LogicalOrToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (LogicalOrToken) t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalLogicalAndExpression = LogicalAndExpression.parse(lexer);
                if (optionalAdditionalLogicalAndExpression.isEmpty()) break parse;
                final var additionalLogicalAndExpression = optionalAdditionalLogicalAndExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalLogicalAndExpression));
            }

            final var result = new LogicalOrExpression(firstLogicalAndExpression, operatorWithExpressionList);
            Logger.info("Matched <LogicalOrExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <LogicalOrExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return firstLogicalAndExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstLogicalAndExpression.detailedRepresentation())
                .append(categoryCode()).append('\n');
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(i.e1().detailedRepresentation())
                    .append(i.e2().detailedRepresentation())
                    .append(categoryCode()).append('\n');
        }
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstLogicalAndExpression.representation());
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.e1().representation())
                    .append(' ').append(i.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<LOrExp>";
    }
}
