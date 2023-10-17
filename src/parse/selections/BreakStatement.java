package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.BreakToken;
import lex.token.SemicolonToken;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class BreakStatement implements SelectionType {
    private final BreakToken breakToken;
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private BreakStatement(BreakToken breakToken, Optional<SemicolonToken> optionalSemicolonToken) {
        this.breakToken = breakToken;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static Optional<BreakStatement> parse(LexerType lexer) {
        Logger.info("Matching <BreakStatement>.");
        final var beginnningPosition = lexer.beginningPosition();

        parse: {
            final var optionalBreakToken = lexer.currentToken()
                    .filter(t -> t instanceof BreakToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (BreakToken) t;
                    });
            if (optionalBreakToken.isEmpty()) break parse;
            final var breakToken = optionalBreakToken.get();

            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            final var result = new BreakStatement(breakToken, optionalSemicolonToken);
            Logger.info("Matched <BreakStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <BreakStatement>.");
        lexer.resetPosition(beginnningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return breakToken.detailedRepresentation()
                + optionalSemicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return breakToken.representation() + " "
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
        return breakToken;
    }
}
