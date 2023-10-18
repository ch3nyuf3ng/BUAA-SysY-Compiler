package parse.nonterminator;

import foundation.Pair;
import lex.protocol.AdditiveTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AdditiveExpression implements NonTerminatorType {
    private final MultiplicativeExpression multiplicativeExpression;
    private final List<Pair<AdditiveTokenType, MultiplicativeExpression>> operatorWithExpressionList;

    private AdditiveExpression(
            MultiplicativeExpression multiplicativeExpression,
            List<Pair<AdditiveTokenType, MultiplicativeExpression>> operatorWithExpressionList
    ) {
        this.multiplicativeExpression = Objects.requireNonNull(multiplicativeExpression);
        this.operatorWithExpressionList = Objects.requireNonNull(operatorWithExpressionList);
    }

    public static Optional<AdditiveExpression> parse(LexerType lexer) {
        Logger.info("Matching <AdditiveExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalMultiplicativeExpression = MultiplicativeExpression.parse(lexer);
            if (optionalMultiplicativeExpression.isEmpty()) break parse;
            final var multiplicativeExpression = optionalMultiplicativeExpression.get();

            final var operatorWithExpressionList = new ArrayList<Pair<AdditiveTokenType, MultiplicativeExpression>>();
            while (true) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(AdditiveTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalMultiplicativeExpression = MultiplicativeExpression.parse(lexer);
                if (additionalMultiplicativeExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalMultiplicativeExpression.get()));
            }

            final var result = new AdditiveExpression(multiplicativeExpression, operatorWithExpressionList);
            Logger.info("Matched <AdditiveExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <AdditiveExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(multiplicativeExpression.detailedRepresentation()).append(categoryCode()).append('\n');
        operatorWithExpressionList.forEach(item -> stringBuilder.append(item.e1().detailedRepresentation())
                .append(item.e2().detailedRepresentation()).append(categoryCode()).append('\n'));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(multiplicativeExpression.representation());
        operatorWithExpressionList.forEach(item -> stringBuilder.append(' ').append(item.e1().representation())
                .append(' ').append(item.e2().representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<AddExp>";
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return multiplicativeExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }
}
