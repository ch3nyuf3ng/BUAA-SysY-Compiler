package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.MinusToken;
import lex.token.PlusToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdditiveExpression implements NonTerminatorType {
    private final MultiplicativeExpression multiplicativeExpression;
    private final List<Pair<TokenType, MultiplicativeExpression>> operatorWithExpressionList;

    private AdditiveExpression(
            MultiplicativeExpression multiplicativeExpression,
            List<Pair<TokenType, MultiplicativeExpression>> operatorWithExpressionList
    ) {
        this.multiplicativeExpression = multiplicativeExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<AdditiveExpression> parse(LexerType lexer) {
        Logger.info("Matching <AdditiveExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalMultiplicativeExpression = MultiplicativeExpression.parse(lexer);
            if (optionalMultiplicativeExpression.isEmpty()) break parse;
            final var multiplicativeExpression = optionalMultiplicativeExpression.get();

            final List<Pair<TokenType, MultiplicativeExpression>> operatorWithExpressionList = new ArrayList<>();
            while (true) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof PlusToken || t instanceof MinusToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalMultiplicativeExpression = MultiplicativeExpression.parse(lexer);
                if (optionalAdditionalMultiplicativeExpression.isEmpty()) break parse;
                final var additionalMultiplicativeExpression = optionalAdditionalMultiplicativeExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalMultiplicativeExpression));
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
        stringBuilder
                .append(multiplicativeExpression.detailedRepresentation())
                .append(categoryCode()).append('\n');
        for (final var item : operatorWithExpressionList) {
            stringBuilder
                    .append(item.e1().detailedRepresentation())
                    .append(item.e2().detailedRepresentation())
                    .append(categoryCode()).append('\n');
        }
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(multiplicativeExpression.representation());
        for (final var item : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(item.e1().representation())
                    .append(' ').append(item.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<AddExp>";
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return multiplicativeExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }
}
