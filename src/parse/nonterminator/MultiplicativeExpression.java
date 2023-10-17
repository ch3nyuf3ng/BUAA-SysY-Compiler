package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.DivideToken;
import lex.token.ModulusToken;
import lex.token.MultiplyToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiplicativeExpression implements NonTerminatorType {
    private final UnaryExpression firstExpression;
    private final List<Pair<TokenType, UnaryExpression>> operatorWithExpressionList;

    private MultiplicativeExpression(
            UnaryExpression firstExpression,
            List<Pair<TokenType, UnaryExpression>> operatorWithExpressionList
    ) {
        this.firstExpression = firstExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<MultiplicativeExpression> parse(LexerType lexer) {
        Logger.info("Matching <MultiplicativeExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalUnaryExpression = UnaryExpression.parse(lexer);
            if (optionalUnaryExpression.isEmpty()) break parse;
            final var unaryExpression = optionalUnaryExpression.get();

            final var operatorWithExpressionList = new ArrayList<Pair<TokenType, UnaryExpression>>();
            while (true) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof MultiplyToken
                                || t instanceof DivideToken
                                || t instanceof ModulusToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalUnaryExpression = UnaryExpression.parse(lexer);
                if (optionalAdditionalUnaryExpression.isEmpty()) break parse;
                final var additionalUnaryExpression = optionalAdditionalUnaryExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalUnaryExpression));
            }

            final var result = new MultiplicativeExpression(unaryExpression, operatorWithExpressionList);
            Logger.info("Matched <MultiplicativeExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <MultiplicativeExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return firstExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstExpression.detailedRepresentation())
                .append(categoryCode()).append('\n');
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
                .append(firstExpression.representation());
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.e1().representation())
                    .append(' ').append(i.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<MulExp>";
    }
}
