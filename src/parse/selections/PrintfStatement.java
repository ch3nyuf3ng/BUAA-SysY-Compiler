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
            Optional<SemicolonToken> optionalSemicolonToken
    ) {
        this.printfToken = printfToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.literalFormatStringToken = literalFormatStringToken;
        this.commaWithExpressionList = commaWithExpressionList;
        this.rightParenthesisToken = rightParenthesisToken;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(PrintfToken.class);
    }

    public static Optional<PrintfStatement> parse(LexerType lexer) {
        Logger.info("Matching <PrintfStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var printfToken = lexer.tryMatchAndConsumeTokenOf(PrintfToken.class);
            if (printfToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var literalFormatStringToken = lexer.tryMatchAndConsumeTokenOf(LiteralFormatStringToken.class);
            if (literalFormatStringToken.isEmpty()) break parse;

            final var commaWithExpressionList = new ArrayList<CommaWith<Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var expression = Expression.parse(lexer);
                if (expression.isEmpty()) break;

                commaWithExpressionList.add(new CommaWith<>(commaToken.get(), expression.get()));
            }

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new PrintfStatement(
                    printfToken.get(),
                    leftParenthesisToken.get(),
                    literalFormatStringToken.get(),
                    commaWithExpressionList,
                    rightParenthesisToken.get(),
                    semicolonToken
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
        stringBuilder.append(printfToken.detailedRepresentation()).append(leftParenthesisToken.detailedRepresentation())
                .append(literalFormatStringToken.detailedRepresentation());
        commaWithExpressionList.forEach(i -> stringBuilder.append(i.commaToken().detailedRepresentation())
                .append(i.entity().detailedRepresentation()));
        stringBuilder.append(rightParenthesisToken.detailedRepresentation());
        optionalSemicolonToken.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(printfToken.representation()).append(leftParenthesisToken.representation()).append(
                literalFormatStringToken.representation());
        commaWithExpressionList.forEach(i -> stringBuilder.append(i.commaToken().representation()).append(' ')
                .append(i.entity().representation()));
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
