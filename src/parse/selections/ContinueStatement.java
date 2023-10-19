package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ContinueToken;
import lex.token.SemicolonToken;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class ContinueStatement implements SelectionType {
    private final ContinueToken continueToken;
    private final Optional<SemicolonToken> semicolonToken;

    public ContinueStatement(ContinueToken continueToken, Optional<SemicolonToken> semicolonToken) {
        this.continueToken = Objects.requireNonNull(continueToken);
        this.semicolonToken = Objects.requireNonNull(semicolonToken);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(ContinueToken.class);
    }

    public static Optional<ContinueStatement> parse(LexerType lexer) {
        Logger.info("Matching <ContinueStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var continueToken = lexer.tryMatchAndConsumeTokenOf(ContinueToken.class);
            if (continueToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new ContinueStatement(continueToken.get(), semicolonToken);
            Logger.info("Matched <ContinueStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ContinueStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return continueToken.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public String representation() {
        return continueToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        return continueToken;
    }
}
