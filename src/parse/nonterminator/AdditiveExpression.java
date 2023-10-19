package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.AdditiveTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.*;

public class AdditiveExpression implements NonTerminatorType {
    private final MultiplicativeExpression firstExpression;
    private final List<Pair<AdditiveTokenType, MultiplicativeExpression>> operatorWithExpressionList;

    public AdditiveExpression(
            MultiplicativeExpression firstExpression,
            List<Pair<AdditiveTokenType, MultiplicativeExpression>> operatorWithExpressionList
    ) {
        this.firstExpression = Objects.requireNonNull(firstExpression);
        this.operatorWithExpressionList = Collections.unmodifiableList(operatorWithExpressionList);
    }

    public static Optional<AdditiveExpression> parse(LexerType lexer) {
        Logger.info("Matching <AdditiveExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstExpression = MultiplicativeExpression.parse(lexer);
            if (firstExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<AdditiveTokenType, MultiplicativeExpression>>();
            while (true) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(AdditiveTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalMultiplicativeExpression = MultiplicativeExpression.parse(lexer);
                if (additionalMultiplicativeExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalMultiplicativeExpression.get()));
            }

            final var result = new AdditiveExpression(firstExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <AdditiveExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <AdditiveExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatorExpressionWithCategoryCodeForEachPairDetailedRepresentation(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(
                firstExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<AddExp>";
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }
}
