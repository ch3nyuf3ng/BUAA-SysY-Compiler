package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.GreaterOrEqualToken;
import lex.token.GreaterToken;
import lex.token.LessOrEqualToken;
import lex.token.LessToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelationalExpression implements NonTerminatorType {
    private final AdditiveExpression firstAdditiveExpression;
    private final List<Pair<TokenType, AdditiveExpression>> operatorWithExpressionList;

    private RelationalExpression(
            AdditiveExpression firstAdditiveExpression,
            List<Pair<TokenType, AdditiveExpression>> operatorWithExpressionList
    ) {
        this.firstAdditiveExpression = firstAdditiveExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<RelationalExpression> parse(LexerType lexer) {
        Logger.info("Matching <RelationalExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalFirstAdditiveExpression = AdditiveExpression.parse(lexer);
            if (optionalFirstAdditiveExpression.isEmpty()) break parse;
            final var firstAdditiveExpression = optionalFirstAdditiveExpression.get();

            final var operatorWithExpressionList = new ArrayList<Pair<TokenType, AdditiveExpression>>();
            while (lexer.currentToken().isPresent()) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof LessToken
                                || t instanceof GreaterToken
                                || t instanceof LessOrEqualToken
                                || t instanceof GreaterOrEqualToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalAdditiveExpression = AdditiveExpression.parse(lexer);
                if (optionalAdditionalAdditiveExpression.isEmpty()) break parse;
                final var additionalAdditiveExpression = optionalAdditionalAdditiveExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalAdditiveExpression));
            }

            final var result = new RelationalExpression(firstAdditiveExpression, operatorWithExpressionList);
            Logger.info("Matched <RelationalExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <RelationalExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return firstAdditiveExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstAdditiveExpression.detailedRepresentation())
                .append(categoryCode()).append('\n');;
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(i.e1().detailedRepresentation())
                    .append(i.e2().detailedRepresentation())
                    .append(categoryCode()).append('\n');;
        }
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstAdditiveExpression.representation());
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.e1().representation())
                    .append(' ').append(i.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<RelExp>";
    }
}
