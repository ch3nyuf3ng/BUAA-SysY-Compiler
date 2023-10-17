package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ScalarVarInitValue implements SelectionType {
    private final Expression expression;

    public ScalarVarInitValue(Expression expression) {
        this.expression = expression;
    }

    public static Optional<ScalarVarInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ScalarVarInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final Expression expression;
            final var optionalExpression = Expression.parse(lexer);
            if (optionalExpression.isPresent()) {
                expression = optionalExpression.get();
            } else {
                break parse;
            }

            final var result = new ScalarVarInitValue(expression);
            Logger.info("Matched <ScalarVarInitValue>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ScalarVarInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return expression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return expression.representation();
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
