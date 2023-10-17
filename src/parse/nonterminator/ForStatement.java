package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ForStatement implements SelectionType {
    private final LeftValue leftValue;
    private final AssignToken assignToken;
    private final Expression expression;

    private ForStatement(LeftValue leftValue, AssignToken assignToken, Expression expression) {
        this.leftValue = leftValue;
        this.assignToken = assignToken;
        this.expression = expression;
    }

    public static Optional<ForStatement> parse(LexerType lexer) {
        Logger.info("Matching <ForStatement>.");
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

            final var result = new ForStatement(leftValue, assignToken, expression);
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
        return leftValue.detailedRepresentation() +
                assignToken.detailedRepresentation() +
                expression.detailedRepresentation() +
                categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return leftValue.representation() + ' '
                + assignToken.representation() + ' '
                + expression.representation();
    }

    @Override
    public String categoryCode() {
        return "<ForStmt>";
    }
}
