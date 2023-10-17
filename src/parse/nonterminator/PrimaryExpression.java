package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftParenthesisToken;
import lex.token.LiteralIntegerToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.ParenthesisedPrimeExpression;
import tests.foundations.Logger;

import java.util.Optional;

public class PrimaryExpression implements NonTerminatorType, SelectionType {
    private final SelectionType primaryExpression;

    private PrimaryExpression(SelectionType primaryExpression) {
        this.primaryExpression = primaryExpression;
    }

    public static Optional<PrimaryExpression> parse(LexerType lexer) {
        Logger.info("Matching <PrimaryExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        final var optionalLeftParenthesisToken = lexer.currentToken()
                .filter(t -> t instanceof LeftParenthesisToken);
        if (optionalLeftParenthesisToken.isPresent()) {
            final var optionalParenthesisedPrimeExpression = ParenthesisedPrimeExpression.parse(lexer);
            if (optionalParenthesisedPrimeExpression.isPresent()) {
                final var parenthesisedPrimeExpression = optionalParenthesisedPrimeExpression.get();
                final var result = new PrimaryExpression(parenthesisedPrimeExpression);
                Logger.info("Matched <PrimaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalIdentifierToken = lexer.currentToken()
                .filter(t -> t instanceof IdentifierToken);
        if (optionalIdentifierToken.isPresent()) {
            final var optionalLeftValue = LeftValue.parse(lexer);
            if (optionalLeftValue.isPresent()) {
                final var leftValue = optionalLeftValue.get();
                final var result = new PrimaryExpression(leftValue);
                Logger.info("Matched <PrimaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalLiteralIntegerToken = lexer.currentToken()
                .filter(t -> t instanceof LiteralIntegerToken);
        if (optionalLiteralIntegerToken.isPresent()) {
            final var optionalNumber = Number.parse(lexer);
            if (optionalNumber.isPresent()) {
                final var number = optionalNumber.get();
                final var result = new PrimaryExpression(number);
                Logger.info("Matched <PrimaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <PrimaryExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return primaryExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return primaryExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<PrimaryExp>";
    }

    @Override
    public TokenType lastTerminator() {
        return primaryExpression.lastTerminator();
    }
}
