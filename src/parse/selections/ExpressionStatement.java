package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ExpressionStatement implements SelectionType {
    private final Optional<Expression> expression;
    private final SemicolonToken semicolonToken;

    public ExpressionStatement(Optional<Expression> expression, SemicolonToken semicolonToken) {
        this.expression = expression;
        this.semicolonToken = semicolonToken;
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(LeftParenthesisToken.class) || lexer.isMatchedTokenOf(LiteralIntegerToken.class)
                || lexer.isMatchedTokenOf(PlusToken.class) || lexer.isMatchedTokenOf(MinusToken.class)
                || lexer.isMatchedTokenOf(LogicalNotToken.class) || lexer.isMatchedTokenOf(SemicolonToken.class)
                || lexer.isMatchedTokenOf(IdentifierToken.class);
    }

    public static Optional<ExpressionStatement> parse(LexerType lexer) {
        Logger.info("Matching <ExpressionStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = Expression.parse(lexer);
            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            if (semicolonToken.isEmpty()) break parse;

            final var result = new ExpressionStatement(expression, semicolonToken.get());
            Logger.info("Matched <ExpressionStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ExpressionStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return expression.map(value -> value.detailedRepresentation() + semicolonToken.detailedRepresentation())
                .orElseGet(semicolonToken::detailedRepresentation);
    }

    @Override
    public String representation() {
        return expression.map(value -> value.representation() + semicolonToken.representation()).orElseGet(
                semicolonToken::representation);
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return semicolonToken;
    }
}
