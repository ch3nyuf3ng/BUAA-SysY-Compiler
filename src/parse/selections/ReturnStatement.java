package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ReturnToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class ReturnStatement implements SelectionType {
    private final ReturnToken returnToken;
    private final Optional<Expression> expression;
    private final SemicolonToken semicolonToken;

    public ReturnStatement(
            ReturnToken returnToken, Optional<Expression> expression, SemicolonToken semicolonToken
    ) {
        this.returnToken = Objects.requireNonNull(returnToken);
        this.expression = Objects.requireNonNull(expression);
        this.semicolonToken = Objects.requireNonNull(semicolonToken);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(ReturnToken.class);
    }

    public static Optional<ReturnStatement> parse(LexerType lexer) {
        Logger.info("Matching <ReturnStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var returnToken = lexer.tryMatchAndConsumeTokenOf(ReturnToken.class);
            if (returnToken.isEmpty()) break parse;

            final var expression = Expression.parse(lexer);

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken.isEmpty()) break parse;

            final var result = new ReturnStatement(returnToken.get(), expression, semicolonToken.get());
            Logger.info("Matched <ReturnStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ReturnStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return returnToken.detailedRepresentation()
                + expression.map(Expression::detailedRepresentation).orElse("")
                + semicolonToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return expression.map(expression -> returnToken.representation() + " "
                + expression.representation()
                + semicolonToken.representation()
                ).orElse("return;");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return null;
    }
}
