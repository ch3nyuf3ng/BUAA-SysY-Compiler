package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ExpressionStatement implements SelectionType {
    private final Optional<Expression> optionalExpression;
    private final Optional<SemicolonToken> optionalSemicolonToken;

    public ExpressionStatement(
            Optional<Expression> optionalExpression,
            Optional<SemicolonToken> optionalSemicolonToken
    ) {
        this.optionalExpression = optionalExpression;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static Optional<ExpressionStatement> parse(LexerType lexer) {
        Logger.info("Matching <ExpressionStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalExpression = Expression.parse(lexer);

            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            if (optionalExpression.isEmpty() && optionalSemicolonToken.isEmpty()) break parse;

            final var result = new ExpressionStatement(optionalExpression, optionalSemicolonToken);
            Logger.info("Matched <ExpressionStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ExpressionStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        if (optionalExpression.isPresent()) {
            return optionalExpression.get().detailedRepresentation()
                    + optionalSemicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
        }
        assert optionalSemicolonToken.isPresent();
        return optionalSemicolonToken.get().detailedRepresentation();
    }

    @Override
    public String representation() {
        if (optionalExpression.isPresent()) {
            return optionalExpression.get().representation()
                    + optionalSemicolonToken.map(SemicolonToken::representation).orElse("");
        }
        assert optionalSemicolonToken.isPresent();
        return optionalSemicolonToken.get().representation();
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
        assert optionalExpression.isPresent();
        return optionalExpression.get().lastTerminator();
    }
}
