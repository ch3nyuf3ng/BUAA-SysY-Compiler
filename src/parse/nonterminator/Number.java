package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LiteralIntegerToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class Number implements NonTerminatorType, SelectionType {
    private final LiteralIntegerToken literalIntegerToken;

    private Number(LiteralIntegerToken literalIntegerToken) {
        this.literalIntegerToken = literalIntegerToken;
    }

    public static Optional<Number> parse(LexerType lexer) {
        Logger.info("Matching <Number>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalLiteralIntegerToken = lexer.currentToken()
                    .filter(t -> t instanceof LiteralIntegerToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LiteralIntegerToken) t;
                    });
            if (optionalLiteralIntegerToken.isEmpty()) break parse;
            final var literalIntegerToken = optionalLiteralIntegerToken.get();

            final var result = new Number(literalIntegerToken);
            Logger.info("Matched <Number>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <Number>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return literalIntegerToken.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return literalIntegerToken.representation();
    }

    @Override
    public String categoryCode() {
        return "<Number>";
    }

    @Override
    public TokenType lastTerminator() {
        return literalIntegerToken;
    }
}