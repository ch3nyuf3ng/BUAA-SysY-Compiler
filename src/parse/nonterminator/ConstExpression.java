package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class ConstExpression implements NonTerminatorType {
    private final AdditiveExpression additiveExpression;

    private ConstExpression(AdditiveExpression additiveExpression) {
        this.additiveExpression = additiveExpression;
    }

    public static Optional<ConstExpression> parse(LexerType lexer) {
        Logger.info("Matching <ConstExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var additiveExpression = AdditiveExpression.parse(lexer);
            if (additiveExpression.isEmpty()) break parse;

            final var result = new ConstExpression(additiveExpression.get());
            Logger.info("Matched <ConstExpression>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ConstExpression>.");
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
        return "<ConstExp>";
    }
}
