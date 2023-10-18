package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.MultiplicativeTokenType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiplicativeExpression implements NonTerminatorType {
    private final UnaryExpression firstExpression;
    private final List<Pair<MultiplicativeTokenType, UnaryExpression>> operatorWithExpressionList;

    private MultiplicativeExpression(
            UnaryExpression firstExpression,
            List<Pair<MultiplicativeTokenType, UnaryExpression>> operatorWithExpressionList
    ) {
        this.firstExpression = firstExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<MultiplicativeExpression> parse(LexerType lexer) {
        Logger.info("Matching <MultiplicativeExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var unaryExpression = UnaryExpression.parse(lexer);
            if (unaryExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<MultiplicativeTokenType, UnaryExpression>>();
            while (true) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(MultiplicativeTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalUnaryExpression = UnaryExpression.parse(lexer);
                if (additionalUnaryExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalUnaryExpression.get()));
            }

            final var result = new MultiplicativeExpression(unaryExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <MultiplicativeExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <MultiplicativeExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.detailedRepresentation()).append(categoryCode()).append('\n');
        operatorWithExpressionList.forEach(i -> stringBuilder.append(i.e1().detailedRepresentation()).append(i.e2()
                .detailedRepresentation()).append(categoryCode()).append('\n'));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.representation());
        operatorWithExpressionList.forEach(i -> stringBuilder.append(' ').append(i.e1().representation()).append(' ')
                .append(i.e2().representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<MulExp>";
    }
}
