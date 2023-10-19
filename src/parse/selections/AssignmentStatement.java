package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.nonterminator.LeftValue;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class AssignmentStatement implements SelectionType {
    private final LeftValue leftValue;
    private final AssignToken assignToken;
    private final Expression expression;
    private final Optional<SemicolonToken> semicolonToken;

    private AssignmentStatement(
            LeftValue leftValue,
            AssignToken assignToken,
            Expression expression,
            Optional<SemicolonToken> semicolonToken
    ) {
        this.leftValue = Objects.requireNonNull(leftValue);
        this.assignToken = Objects.requireNonNull(assignToken);
        this.expression = Objects.requireNonNull(expression);
        this.semicolonToken = Objects.requireNonNull(semicolonToken);
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
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return leftValue.representation()
                + " " + assignToken.representation()
                + " " + expression.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        return expression.lastTerminator();
    }
}