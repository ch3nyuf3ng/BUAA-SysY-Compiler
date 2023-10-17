package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.nonterminator.UnaryExpression;
import parse.nonterminator.UnaryOperator;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class UnaryOperatedExpression implements SelectionType {
    private final UnaryOperator unaryOperator;
    private final UnaryExpression unaryExpression;

    private UnaryOperatedExpression(UnaryOperator unaryOperator, UnaryExpression unaryExpression) {
        this.unaryOperator = unaryOperator;
        this.unaryExpression = unaryExpression;
    }

    public static Optional<UnaryOperatedExpression> parse(LexerType lexer) {
        Logger.info("Matching <UnaryOperatedExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalUnaryOperator = UnaryOperator.parse(lexer);
            if (optionalUnaryOperator.isEmpty()) break parse;
            final var unaryOperator = optionalUnaryOperator.get();

            final var optionalUnaryExpression = UnaryExpression.parse(lexer);
            if (optionalUnaryExpression.isEmpty()) break parse;
            final var unaryExpression = optionalUnaryExpression.get();

            final var result = new UnaryOperatedExpression(unaryOperator, unaryExpression);
            Logger.info("Matched <UnaryOperatedExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <UnaryOperatedExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return unaryOperator.detailedRepresentation() + unaryExpression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return unaryOperator.representation() + unaryExpression.representation();
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
