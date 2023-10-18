package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.EqualityTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EqualityExpression implements NonTerminatorType {
    private final RelationalExpression firstExpression;
    private final List<Pair<EqualityTokenType, RelationalExpression>> operatorWithExpressionList;

    private EqualityExpression(
            RelationalExpression firstExpression,
            List<Pair<EqualityTokenType, RelationalExpression>> operatorWithExpressionList
    ) {
        this.firstExpression = firstExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<EqualityExpression> parse(LexerType lexer) {
        Logger.info("Matching <EqualityExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstExpression = RelationalExpression.parse(lexer);
            if (firstExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<EqualityTokenType, RelationalExpression>>();
            while (lexer.isMatchedTokenOf(EqualityTokenType.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(EqualityTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalRelationalExpression = RelationalExpression.parse(lexer);
                if (additionalRelationalExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalRelationalExpression.get()));
            }

            final var result = new EqualityExpression(firstExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <EqualityExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <EqualityExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<EqExp>";
    }
}
