package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LogicalNotToken;
import lex.token.MinusToken;
import lex.token.PlusToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class UnaryOperator implements NonTerminatorType {
    private final TokenType operator;

    private UnaryOperator(TokenType operator) {
        this.operator = operator;
    }

    public static Optional<UnaryOperator> parse(LexerType lexer) {
        Logger.info("Matching <UnaryOperator>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalOperator = lexer.currentToken()
                    .filter(t -> t instanceof PlusToken
                            || t instanceof MinusToken
                            || t instanceof LogicalNotToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return t;
                    });
            if (optionalOperator.isEmpty()) break parse;
            final var operator = optionalOperator.get();

            final var result = new UnaryOperator(operator);
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
