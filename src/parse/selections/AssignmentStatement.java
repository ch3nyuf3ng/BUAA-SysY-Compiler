package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.nonterminator.LeftValue;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Objects;
import java.util.Optional;

public class AssignmentStatement implements SelectionType {
    private final LeftValue leftValue;
    private final AssignToken assignToken;
    private final Expression expression;
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private AssignmentStatement(
            LeftValue leftValue,
            AssignToken assignToken,
            Expression expression,
            Optional<SemicolonToken> optionalSemicolonToken
    ) {
        this.leftValue = Objects.requireNonNull(leftValue);
        this.assignToken = Objects.requireNonNull(assignToken);
        this.expression = Objects.requireNonNull(expression);
        this.optionalSemicolonToken = Objects.requireNonNull(optionalSemicolonToken);
    }

    @SuppressWarnings("DuplicatedCode")
    public static Optional<AssignmentStatement> parse(LexerType lexer) {
        Logger.info("Matching <AssignmentStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = LeftValue.parse(lexer);
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var expression = Expression.parse(lexer);
            if (expression.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new AssignmentStatement(
                    leftValue.get(), assignToken.get(), expression.get(), semicolonToken
            );
            Logger.info("Matched <AssignmentStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <AssignmentStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation()
                + assignToken.detailedRepresentation()
                + expression.detailedRepresentation()
                + optionalSemicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return leftValue.representation()
                + " " + assignToken.representation()
                + " " + expression.representation()
                + optionalSemicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalSemicolonToken.isPresent()) return optionalSemicolonToken.get();
        return expression.lastTerminator();
    }
}