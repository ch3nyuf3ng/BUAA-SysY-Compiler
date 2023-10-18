package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.*;

public class RelationalExpression implements NonTerminatorType {
    private final AdditiveExpression firstAdditiveExpression;
    private final List<Pair<RelaitionalOperatorTokenType, AdditiveExpression>> operatorWithExpressionList;

    public RelationalExpression(
            AdditiveExpression firstAdditiveExpression,
            List<Pair<RelaitionalOperatorTokenType, AdditiveExpression>> operatorWithExpressionList
    ) {
        this.firstAdditiveExpression = Objects.requireNonNull(firstAdditiveExpression);
        this.operatorWithExpressionList = Collections.unmodifiableList(operatorWithExpressionList);
    }

    public static Optional<RelationalExpression> parse(LexerType lexer) {
        Logger.info("Matching <RelationalExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstAdditiveExpression = AdditiveExpression.parse(lexer);
            if (firstAdditiveExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<RelaitionalOperatorTokenType, AdditiveExpression>>();
            while (lexer.isMatchedTokenOf(RelaitionalOperatorTokenType.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(RelaitionalOperatorTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalAdditiveExpression = AdditiveExpression.parse(lexer);
                if (additionalAdditiveExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalAdditiveExpression.get()));
            }

            final var result = new RelationalExpression(firstAdditiveExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <RelationalExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <RelationalExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstAdditiveExpression.lastTerminator();
        return operatorWithExpressionList.get(operatorWithExpressionList.size() - 1).second().lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                firstAdditiveExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstAdditiveExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<RelExp>";
    }
}
