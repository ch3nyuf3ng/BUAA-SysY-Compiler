package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;
import parse.nonterminator.UnaryExpression;
import parse.nonterminator.UnaryOperator;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class UnaryOperatedExpression implements SelectionType {
    private final UnaryOperator unaryOperator;
    private final UnaryExpression unaryExpression;

    public UnaryOperatedExpression(UnaryOperator unaryOperator, UnaryExpression unaryExpression) {
        this.unaryOperator = Objects.requireNonNull(unaryOperator);
        this.unaryExpression = Objects.requireNonNull(unaryExpression);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(UnaryOperatorTokenType.class);
    }

    public static Optional<UnaryOperatedExpression> parse(LexerType lexer) {
        Logger.info("Matching <UnaryOperatedExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var unaryOperator = UnaryOperator.parse(lexer);
            if (unaryOperator.isEmpty()) break parse;

            final var unaryExpression = UnaryExpression.parse(lexer);
            if (unaryExpression.isEmpty()) break parse;

            final var result = new UnaryOperatedExpression(unaryOperator.get(), unaryExpression.get());
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
