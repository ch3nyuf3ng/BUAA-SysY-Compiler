package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrintfStatement implements SelectionType {
    private final PrintfToken printfToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final LiteralFormatStringToken literalFormatStringToken;
    private final List<CommaWith<Expression>> commaWithExpressionList;
    private final RightParenthesisToken rightParenthesisToken;
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private PrintfStatement(
            PrintfToken printfToken,
            LeftParenthesisToken leftParenthesisToken,
            LiteralFormatStringToken literalFormatStringToken,
            List<CommaWith<Expression>> commaWithExpressionList,
            RightParenthesisToken rightParenthesisToken,
            Optional<SemicolonToken> optionalSemicolonToken) {
        this.printfToken = printfToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.literalFormatStringToken = literalFormatStringToken;
        this.commaWithExpressionList = commaWithExpressionList;
        this.rightParenthesisToken = rightParenthesisToken;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static Optional<PrintfStatement> parse(LexerType lexer) {
        Logger.info("Matching <PrintfStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final PrintfToken printfToken;
            final var optionalPrintfToken = lexer.currentToken()
                    .filter(t -> t instanceof PrintfToken)
                    .map(t -> (PrintfToken) t);
            if (optionalPrintfToken.isPresent()) {
                printfToken = optionalPrintfToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final LeftParenthesisToken leftParenthesisToken;
            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> (LeftParenthesisToken) t);
            if (optionalLeftParenthesisToken.isPresent()) {
                leftParenthesisToken = optionalLeftParenthesisToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final LiteralFormatStringToken literalFormatStringToken;
            final var optionalLiteralFormatStringToken = lexer.currentToken()
                    .filter(t -> t instanceof LiteralFormatStringToken)
                    .map(t -> (LiteralFormatStringToken) t);
            if (optionalLiteralFormatStringToken.isPresent()) {
                literalFormatStringToken = optionalLiteralFormatStringToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final List<CommaWith<Expression>> commaWithExpressionList = new ArrayList<>();
            while (lexer.currentToken().isPresent()) {
                final CommaToken commaToken;
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> (CommaToken) t);
                if (optionalCommaToken.isPresent()) {
                    commaToken = optionalCommaToken.get();
                    lexer.consumeToken();
                } else {
                    break;
                }

                final Expression expression;
                final var optionalExpression = Expression.parse(lexer);
                if (optionalExpression.isPresent()) {
                    expression = optionalExpression.get();
                } else {
                    break parse;
                }

                commaWithExpressionList.add(new CommaWith<>(commaToken, expression));
            }

            final RightParenthesisToken rightParenthesisToken;
            final var optionalRightParenthesis = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> (RightParenthesisToken) t);
            if (optionalRightParenthesis.isPresent()) {
                rightParenthesisToken = optionalRightParenthesis.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            final var result = new PrintfStatement(
                    printfToken,
                    leftParenthesisToken,
                    literalFormatStringToken,
                    commaWithExpressionList,
                    rightParenthesisToken,
                    optionalSemicolonToken
            );
            Logger.info("Matched <PrintfStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <PrintfStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(printfToken.detailedRepresentation())
                .append(leftParenthesisToken.detailedRepresentation())
                .append(literalFormatStringToken.detailedRepresentation());
        for (final var i : commaWithExpressionList) {
            stringBuilder.append(i.commaToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
        }
        stringBuilder.append(rightParenthesisToken.detailedRepresentation());
        optionalSemicolonToken.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(printfToken.representation())
                .append(leftParenthesisToken.representation())
                .append(literalFormatStringToken.representation());
        for (final var i : commaWithExpressionList) {
            stringBuilder
                    .append(i.commaToken().representation()).append(' ')
                    .append(i.entity().representation());
        }
        stringBuilder.append(rightParenthesisToken.representation());
        optionalSemicolonToken.ifPresent(t -> stringBuilder.append(t.representation()));
        return stringBuilder.toString();
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
