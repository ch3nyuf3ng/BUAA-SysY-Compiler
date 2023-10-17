package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ReturnToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ReturnStatement implements SelectionType {
    private final ReturnToken returnToken;
    private final Optional<Expression> optionalExpression;
    private final SemicolonToken semicolonToken;

    private ReturnStatement(ReturnToken returnToken, Optional<Expression> optionalExpression, SemicolonToken semicolonToken) {
        this.returnToken = returnToken;
        this.optionalExpression = optionalExpression;
        this.semicolonToken = semicolonToken;
    }

    public static Optional<ReturnStatement> parse(LexerType lexer) {
        Logger.info("Matching <ReturnStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final ReturnToken returnToken;
            var optionalCurrentToken = lexer.currentToken();
            if (optionalCurrentToken.isPresent() && optionalCurrentToken.get() instanceof ReturnToken) {
                returnToken = (ReturnToken) optionalCurrentToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final Optional<Expression> optionalExpression = Expression.parse(lexer);

            final SemicolonToken semicolonToken;
            optionalCurrentToken = lexer.currentToken();
            if (optionalCurrentToken.isPresent() && optionalCurrentToken.get() instanceof SemicolonToken) {
                semicolonToken = (SemicolonToken) optionalCurrentToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final var result = new ReturnStatement(returnToken, optionalExpression, semicolonToken);
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
                + optionalExpression.map(Expression::detailedRepresentation).orElse("")
                + semicolonToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return returnToken.representation() + " "
                + optionalExpression.map(Expression::representation).orElse("")
                + semicolonToken.representation();
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