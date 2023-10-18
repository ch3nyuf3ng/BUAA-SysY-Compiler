package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.*;

public class FuncArgList implements NonTerminatorType {
    private final Expression firstExpression;
    private final List<Pair<CommaToken, Expression>> commaWithExpressionList;

    private FuncArgList(Expression firstExpression, List<Pair<CommaToken, Expression>> commaWithExpressionList) {
        this.firstExpression = Objects.requireNonNull(firstExpression);
        this.commaWithExpressionList = Collections.unmodifiableList(commaWithExpressionList);
    }

    public static Optional<FuncArgList> parse(LexerType lexer) {
        Logger.info("Matching <FuncArgList>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = Expression.parse(lexer);
            if (expression.isEmpty()) break parse;

            final var commaWithExpressionList = new ArrayList<Pair<CommaToken, Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var additionalExpression = Expression.parse(lexer);
                if (additionalExpression.isEmpty()) break parse;

                commaWithExpressionList.add(new Pair<>(commaToken.get(), additionalExpression.get()));
            }

            final var result = new FuncArgList(expression.get(), commaWithExpressionList);
            Logger.info("Matched <FuncArgList>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncArgList>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (commaWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = commaWithExpressionList.size() - 1;
        final var lastNonTerminator = commaWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                firstExpression, commaWithExpressionList
        ) + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstExpression, commaWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<FuncRParams>";
    }
}
