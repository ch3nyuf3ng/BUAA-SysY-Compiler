package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.nonterminator.LeftValue;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

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
        this.leftValue = leftValue;
        this.assignToken = assignToken;
        this.expression = expression;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static Optional<AssignmentStatement> parse(LexerType lexer) {
        Logger.info("Matching <AssignmentStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalLeftValue = LeftValue.parse(lexer);
            if (optionalLeftValue.isEmpty()) break parse;
            final var leftValue = optionalLeftValue.get();


            final var optionalAssignToken = lexer.currentToken()
                    .filter(t -> t instanceof AssignToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (AssignToken) t;
                    });
            if (optionalAssignToken.isEmpty()) break parse;
            final var assignToken = optionalAssignToken.get();

            final var optionalExpression = Expression.parse(lexer);
            if (optionalExpression.isEmpty()) break parse;
            final var expression = optionalExpression.get();

            final var optionalSemicolon = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            final var result = new AssignmentStatement(leftValue, assignToken, expression, optionalSemicolon);
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
        return leftValue.representation() + " "
                + assignToken.representation() + " "
                + expression.representation()
                + optionalSemicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalSemicolonToken.isPresent()) {
            return optionalSemicolonToken.get();
        }
        return expression.lastTerminator();
    }
}