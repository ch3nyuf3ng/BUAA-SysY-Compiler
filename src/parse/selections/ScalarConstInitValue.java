package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.nonterminator.ConstExpression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ScalarConstInitValue implements SelectionType {
    private final ConstExpression constExpression;

    private ScalarConstInitValue(ConstExpression constExpression) {
        this.constExpression = constExpression;
    }

    public static Optional<ScalarConstInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ScalarConstInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final ConstExpression constExpression;
            final var optionalConstExpression = ConstExpression.parse(lexer);
            if (optionalConstExpression.isPresent()) {
                constExpression = optionalConstExpression.get();
            } else {
                break parse;
            }

            final var result = new ScalarConstInitValue(constExpression);
            Logger.info("Matched <ScalarConstInitValue>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ScalarConstInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return constExpression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return constExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return constExpression.lastTerminator();
    }
}
