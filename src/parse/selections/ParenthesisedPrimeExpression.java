package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftParenthesisToken;
import lex.token.RightParenthesisToken;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ParenthesisedPrimeExpression implements SelectionType {
    private final LeftParenthesisToken leftParenthesisToken;
    private final Expression expression;
    private final RightParenthesisToken rightParenthesisToken;

    public ParenthesisedPrimeExpression(
            LeftParenthesisToken leftParenthesisToken,
            Expression expression,
            RightParenthesisToken rightParenthesisToken
    ) {
        this.leftParenthesisToken = leftParenthesisToken;
        this.expression = expression;
        this.rightParenthesisToken = rightParenthesisToken;
    }

    public static Optional<ParenthesisedPrimeExpression> parse(LexerType lexer) {
        Logger.info("Matching <ParenthesisedPrimeExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final LeftParenthesisToken leftParenthesisToken;
            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> (LeftParenthesisToken) t);
            if (optionalLeftParenthesisToken.isPresent()) {
                leftParenthesisToken = optionalLeftParenthesisToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final Expression expression;
            final var optionalExpression = Expression.parse(lexer);
            if (optionalExpression.isPresent()) {
                expression = optionalExpression.get();
            } else {
                break parse;
            }

            final RightParenthesisToken rightParenthesisToken;
            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> (RightParenthesisToken) t);
            if (optionalRightParenthesisToken.isPresent()) {
                rightParenthesisToken = optionalRightParenthesisToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final var result = new ParenthesisedPrimeExpression(
                    leftParenthesisToken,
                    expression,
                    rightParenthesisToken
            );
            Logger.info("Matched <ParenthesisedPrimeExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ParenthesisedPrimeExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return leftParenthesisToken.detailedRepresentation()
                + expression.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return leftParenthesisToken.representation()
                + expression.representation()
                + rightParenthesisToken.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return null;
    }
}
