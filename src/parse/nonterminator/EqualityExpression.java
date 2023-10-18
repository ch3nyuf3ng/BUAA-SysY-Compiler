package parse.nonterminator;

import foundation.Pair;
import lex.protocol.EqualityTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EqualityExpression implements NonTerminatorType {
    private final RelationalExpression firstRelationalExpression;
    private final List<Pair<EqualityTokenType, RelationalExpression>> operatorWithExpressionList;

    private EqualityExpression(
            RelationalExpression firstRelationalExpression,
            List<Pair<EqualityTokenType, RelationalExpression>> operatorWithExpressionList
    ) {
        this.firstRelationalExpression = firstRelationalExpression;
        this.operatorWithExpressionList = operatorWithExpressionList;
    }

    public static Optional<EqualityExpression> parse(LexerType lexer) {
        Logger.info("Matching <EqualityExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstRelationalExpression = RelationalExpression.parse(lexer);
            if (firstRelationalExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<EqualityTokenType, RelationalExpression>>();
            while (lexer.isMatchedTokenOf(EqualityTokenType.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(EqualityTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalRelationalExpression = RelationalExpression.parse(lexer);
                if (additionalRelationalExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalRelationalExpression.get()));
            }

            final var result = new EqualityExpression(firstRelationalExpression.get(), operatorWithExpressionList);
            Logger.info("Matched <EqualityExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <EqualityExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstRelationalExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).e2();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstRelationalExpression.detailedRepresentation()).append(categoryCode()).append('\n');
        operatorWithExpressionList.forEach(i -> stringBuilder.append(i.e1().detailedRepresentation()).append(i.e2()
                .detailedRepresentation()).append(categoryCode()).append('\n'));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstRelationalExpression.representation());
        operatorWithExpressionList.forEach(i -> stringBuilder.append(' ').append(i.e1().representation()).append(' ')
                .append(i.e2().representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<EqExp>";
    }
}
