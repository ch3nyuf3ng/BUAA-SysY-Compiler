package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ContinueToken;
import lex.token.SemicolonToken;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ContinueStatement implements SelectionType {
    private final ContinueToken continueToken;
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private ContinueStatement(ContinueToken continueToken, Optional<SemicolonToken> optionalSemicolonToken) {
        this.continueToken = continueToken;
        this.optionalSemicolonToken = optionalSemicolonToken;
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
                + optionalSemicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public String representation() {
        return continueToken.representation() + " "
                + optionalSemicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalSemicolonToken.isPresent()) return optionalSemicolonToken.get();
        return continueToken;
    }
}
