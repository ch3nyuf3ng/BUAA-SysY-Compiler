package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class Expression implements NonTerminatorType {
    private final AdditiveExpression additiveExpression;

    private Expression(AdditiveExpression additiveExpression) {
        this.additiveExpression = additiveExpression;
    }

    public static Optional<Expression> parse(LexerType lexer) {
        Logger.info("Matching <Expression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalAdditiveExpression = AdditiveExpression.parse(lexer);
            if (optionalAdditiveExpression.isEmpty()) break parse;
            final var additiveExpression = optionalAdditiveExpression.get();

            final var result = new Expression(additiveExpression);
            Logger.info("Matched <Expression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <Expression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return additiveExpression.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return additiveExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return additiveExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<Exp>";
    }
}
