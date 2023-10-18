package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Objects;
import java.util.Optional;

public class ForStatement implements SelectionType {
    private final LeftValue leftValue;
    private final AssignToken assignToken;
    private final Expression expression;

    public ForStatement(LeftValue leftValue, AssignToken assignToken, Expression expression) {
        this.leftValue = Objects.requireNonNull(leftValue);
        this.assignToken = Objects.requireNonNull(assignToken);
        this.expression = Objects.requireNonNull(expression);
    }

    public static Optional<ForStatement> parse(LexerType lexer) {
        Logger.info("Matching <ForStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = LeftValue.parse(lexer);
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var expression = Expression.parse(lexer);
            if (expression.isEmpty()) break parse;

            final var result = new ForStatement(leftValue.get(), assignToken.get(), expression.get());
            Logger.info("Matched <ForStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ForStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return expression.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation() + assignToken.detailedRepresentation()
                + expression.detailedRepresentation() + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return leftValue.representation() + ' ' + assignToken.representation() + ' ' + expression.representation();
    }

    @Override
    public String categoryCode() {
        return "<ForStmt>";
    }
}
