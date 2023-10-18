package parse.selections;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.nonterminator.Expression;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrintfStatement implements SelectionType {
    private final PrintfToken printfToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final LiteralFormatStringToken literalFormatStringToken;
    private final List<Pair<CommaToken, Expression>> commaWithExpressionList;
    private final RightParenthesisToken rightParenthesisToken;
    private final Optional<SemicolonToken> semicolonToken;

    private PrintfStatement(
            PrintfToken printfToken,
            LeftParenthesisToken leftParenthesisToken,
            LiteralFormatStringToken literalFormatStringToken,
            List<Pair<CommaToken, Expression>> commaWithExpressionList,
            RightParenthesisToken rightParenthesisToken,
            Optional<SemicolonToken> semicolonToken
    ) {
        this.printfToken = printfToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.literalFormatStringToken = literalFormatStringToken;
        this.commaWithExpressionList = commaWithExpressionList;
        this.rightParenthesisToken = rightParenthesisToken;
        this.semicolonToken = semicolonToken;
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

            final var commaWithExpressionList = new ArrayList<Pair<CommaToken, Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var expression = Expression.parse(lexer);
                if (expression.isEmpty()) break;

                commaWithExpressionList.add(new Pair<>(commaToken.get(), expression.get()));
            }

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new PrintfStatement(
                    printfToken.get(), leftParenthesisToken.get(),
                    literalFormatStringToken.get(), commaWithExpressionList,
                    rightParenthesisToken.get(), semicolonToken
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
        return printfToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return printfToken.representation()
                + leftParenthesisToken.representation()
                + RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
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
