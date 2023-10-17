package parse.nonterminator;

import foundation.Pair;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.EqualToken;
import lex.token.NotEqualToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EqualityExpression implements NonTerminatorType {
    private final RelationalExpression firstRelationalExpression;
    private final List<Pair<TokenType, RelationalExpression>> operatorWithExpressionList;

    private EqualityExpression(
            RelationalExpression firstRelationalExpression,
            List<Pair<TokenType, RelationalExpression>> operatorWithExpressionList
    ) {
        this.firstRelationalExpression = firstRelationalExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<EqualityExpression> parse(LexerType lexer) {
        Logger.info("Matching <EqualityExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalFirstRelationalExpression = RelationalExpression.parse(lexer);
            if (optionalFirstRelationalExpression.isEmpty()) break parse;
            final var firstRelationalExpression = optionalFirstRelationalExpression.get();

            final List<Pair<TokenType, RelationalExpression>> operatorWithExpressionList = new ArrayList<>();
            while (true) {
                final var optionalOperator = lexer.currentToken()
                        .filter(t -> t instanceof EqualToken || t instanceof NotEqualToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return t;
                        });
                if (optionalOperator.isEmpty()) break;
                final var operator = optionalOperator.get();

                final var optionalAdditionalRelationalExpression = RelationalExpression.parse(lexer);
                if (optionalAdditionalRelationalExpression.isEmpty()) break parse;
                final var additionalRelationalExpression = optionalAdditionalRelationalExpression.get();

                operatorWithExpressionList.add(new Pair<>(operator, additionalRelationalExpression));
            }

            final var result = new EqualityExpression(firstRelationalExpression, operatorWithExpressionList);
            Logger.info("Matched <EqualityExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <EqualityExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) {
            return firstRelationalExpression.lastTerminator();
        }
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(firstRelationalExpression.detailedRepresentation())
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
        stringBuilder.append(firstRelationalExpression.representation());
        for (final var i : operatorWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.e1().representation())
                    .append(' ').append(i.e2().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<EqExp>";
    }
}
