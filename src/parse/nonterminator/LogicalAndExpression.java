package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LogicalAndToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogicalAndExpression implements NonTerminatorType {
    private final EqualityExpression firstEqualityExpression;
    private final List<Pair<LogicalAndToken, EqualityExpression>> operatorWithExpressionList;

    private LogicalAndExpression(
            EqualityExpression firstEqualityExpression,
            List<Pair<LogicalAndToken, EqualityExpression>> operatorWithExpressionList
    ) {
        this.firstEqualityExpression = firstEqualityExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<LogicalAndExpression> parse(LexerType lexer) {
        Logger.info("Matching <LogicalAndExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalFirstEquality = EqualityExpression.parse(lexer);
            if (optionalFirstEquality.isEmpty()) break parse;
            final var firstEqualityExpression = optionalFirstEquality.get();

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalAndToken, EqualityExpression>>();
            while (lexer.currentToken().isPresent()) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof LogicalAndToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (LogicalAndToken) t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalEqualityExpression = EqualityExpression.parse(lexer);
                if (optionalAdditionalEqualityExpression.isEmpty()) break parse;
                final var additionalEqualityExpression = optionalAdditionalEqualityExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalEqualityExpression));
            }

            final var result = new LogicalAndExpression(firstEqualityExpression, operatorWithExpressionList);
            Logger.info("Matched <LogicalAndExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <LogicalAndExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return firstEqualityExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstEqualityExpression.detailedRepresentation())
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
        stringBuilder.append(firstEqualityExpression.representation());
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.e1().representation())
                    .append(' ').append(i.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<LAndExp>";
    }
}
