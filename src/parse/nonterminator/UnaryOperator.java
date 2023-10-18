package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class UnaryOperator implements NonTerminatorType {
    private final UnaryOperatorTokenType operator;

    private UnaryOperator(UnaryOperatorTokenType operator) {
        this.operator = operator;
    }

    public static Optional<UnaryOperator> parse(LexerType lexer) {
        Logger.info("Matching <UnaryOperator>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var operator = lexer.tryMatchAndConsumeTokenOf(UnaryOperatorTokenType.class);
            if (operator.isEmpty()) break parse;

            final var result = new UnaryOperator(operator.get());
            Logger.info("Matched <UnaryOperator>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <UnaryOperator>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return operator.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return operator.representation();
    }

    @Override
    public String categoryCode() {
        return "<UnaryOp>";
    }

    @Override
    public TokenType lastTerminator() {
        return operator;
    }
}
