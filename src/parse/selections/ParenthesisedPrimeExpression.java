package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftParenthesisToken;
import lex.token.RightParenthesisToken;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Objects;
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
        this.leftParenthesisToken = Objects.requireNonNull(leftParenthesisToken);
        this.expression = Objects.requireNonNull(expression);
        this.rightParenthesisToken = Objects.requireNonNull(rightParenthesisToken);
    }

    public static Optional<ParenthesisedPrimeExpression> parse(LexerType lexer) {
        Logger.info("Matching <ParenthesisedPrimeExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var expression = Expression.parse(lexer);
            if (expression.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var result = new ParenthesisedPrimeExpression(
                    leftParenthesisToken.get(),
                    expression.get(),
                    rightParenthesisToken.get()
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
