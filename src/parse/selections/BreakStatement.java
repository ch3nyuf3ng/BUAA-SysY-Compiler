package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.BreakToken;
import lex.token.SemicolonToken;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class BreakStatement implements SelectionType {
    private final BreakToken breakToken;
    private final Optional<SemicolonToken> semicolonToken;

    public BreakStatement(BreakToken breakToken, Optional<SemicolonToken> semicolonToken) {
        this.breakToken = Objects.requireNonNull(breakToken);
        this.semicolonToken = Objects.requireNonNull(semicolonToken);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(BreakToken.class);
    }

    public static Optional<BreakStatement> parse(LexerType lexer) {
        Logger.info("Matching <BreakStatement>.");
        final var beginnningPosition = lexer.beginningPosition();

        parse:
        {
            final var breakToken = lexer.tryMatchAndConsumeTokenOf(BreakToken.class);
            if (breakToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new BreakStatement(breakToken.get(), semicolonToken);
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
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return breakToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        return breakToken;
    }
}
