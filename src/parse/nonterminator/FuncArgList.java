package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncArgList implements NonTerminatorType {
    private final Expression firstExpression;
    private final List<CommaWith<Expression>> commaWithExpressionList;

    private FuncArgList(Expression firstExpression, List<CommaWith<Expression>> commaWithExpressionList) {
        this.firstExpression = firstExpression;
        this.commaWithExpressionList = commaWithExpressionList;
    }

    public static Optional<FuncArgList> parse(LexerType lexer) {
        Logger.info("Matching <FuncArgList>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalExpression = Expression.parse(lexer);
            if (optionalExpression.isEmpty()) break parse;
            final var expression = optionalExpression.get();

            final List<CommaWith<Expression>> commaWithExpressionList = new ArrayList<>();
            while (true) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalAdditionalExpression = Expression.parse(lexer);
                if (optionalAdditionalExpression.isEmpty()) break parse;
                final var additionalExpression = optionalAdditionalExpression.get();

                commaWithExpressionList.add(new CommaWith<>(commaToken, additionalExpression));
            }

            final var result = new FuncArgList(expression, commaWithExpressionList);
            Logger.info("Matched <FuncArgList>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncArgList>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (commaWithExpressionList.isEmpty()) {
            return firstExpression.lastTerminator();
        }
        final var lastIndex = commaWithExpressionList.size() - 1;
        final var lastNonTerminator = commaWithExpressionList.get(lastIndex).entity();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.detailedRepresentation());
        for (final var i : commaWithExpressionList) {
            stringBuilder
                    .append(i.commaToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
        }
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstExpression.representation());
        for (final var i : commaWithExpressionList) {
            stringBuilder
                    .append(' ').append(i.commaToken().representation())
                    .append(' ').append(i.entity().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncRParams>";
    }
}
