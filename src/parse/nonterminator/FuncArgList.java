package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FuncArgList implements NonTerminatorType {
    private final Expression firstExpression;
    private final List<CommaWith<Expression>> commaWithExpressionList;

    private FuncArgList(Expression firstExpression, List<CommaWith<Expression>> commaWithExpressionList) {
        this.firstExpression = Objects.requireNonNull(firstExpression);
        this.commaWithExpressionList = Objects.requireNonNull(commaWithExpressionList);
    }

    public static Optional<FuncArgList> parse(LexerType lexer) {
        Logger.info("Matching <FuncArgList>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = Expression.parse(lexer);
            if (expression.isEmpty()) break parse;

            final var commaWithExpressionList = new ArrayList<CommaWith<Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var additionalExpression = Expression.parse(lexer);
                if (additionalExpression.isEmpty()) break parse;

                commaWithExpressionList.add(new CommaWith<>(commaToken.get(), additionalExpression.get()));
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
        final var lastNonTerminator = commaWithExpressionList.get(lastIndex).entity();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.detailedRepresentation());
        commaWithExpressionList.forEach(i -> stringBuilder.append(i.commaToken().detailedRepresentation())
                .append(i.entity().detailedRepresentation()));
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.representation());
        commaWithExpressionList.forEach(i -> stringBuilder.append(' ').append(i.commaToken().representation())
                .append(' ').append(i.entity().representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncRParams>";
    }
}
