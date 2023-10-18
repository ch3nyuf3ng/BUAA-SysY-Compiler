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

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(LeftParenthesisToken.class) || lexer.isMatchedTokenOf(IdentifierToken.class)
                || lexer.isMatchedTokenOf(LiteralIntegerToken.class);
    }

    public static Optional<PrimaryExpression> parse(LexerType lexer) {
        Logger.info("Matching <PrimaryExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(LeftParenthesisToken.class)) {
            final var parenthesisedPrimeExpression = ParenthesisedPrimeExpression.parse(lexer);
            if (parenthesisedPrimeExpression.isPresent()) {
                final var result = new PrimaryExpression(parenthesisedPrimeExpression.get());
                Logger.info("Matched <PrimaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (lexer.isMatchedTokenOf(IdentifierToken.class)) {
            final var leftValue = LeftValue.parse(lexer);
            if (leftValue.isPresent()) {
                final var result = new PrimaryExpression(leftValue.get());
                Logger.info("Matched <PrimaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (lexer.isMatchedTokenOf(LiteralIntegerToken.class)) {
            final var number = Number.parse(lexer);
            if (number.isPresent()) {
                final var result = new PrimaryExpression(number.get());
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
