package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.nonterminator.LeftValue;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class GetIntStatement implements SelectionType {
    private final LeftValue leftValue;
    private final AssignToken assignToken;
    private final GetIntToken getIntToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final RightParenthesisToken rightParenthesisToken;
    private final SemicolonToken semicolonToken;

    private GetIntStatement(
            LeftValue leftValue,
            AssignToken assignToken,
            GetIntToken getIntToken,
            LeftParenthesisToken leftParenthesisToken,
            RightParenthesisToken rightParenthesisToken,
            SemicolonToken semicolonToken
    ) {
        this.leftValue = leftValue;
        this.assignToken = assignToken;
        this.getIntToken = getIntToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.rightParenthesisToken = rightParenthesisToken;
        this.semicolonToken = semicolonToken;
    }

    public static Optional<GetIntStatement> parse(LexerType lexer) {
        Logger.info("Matching <GetIntStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = LeftValue.parse(lexer);
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var getIntToken = lexer.tryMatchAndConsumeTokenOf(GetIntToken.class);
            if (getIntToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken.isEmpty()) break parse;

            final var result = new GetIntStatement(
                    leftValue.get(),
                    assignToken.get(),
                    getIntToken.get(),
                    leftParenthesisToken.get(),
                    rightParenthesisToken.get(),
                    semicolonToken.get()
            );
            Logger.info("Matched <GetIntStatement>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <GetIntStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation() + assignToken.detailedRepresentation()
                + getIntToken.detailedRepresentation() + leftParenthesisToken.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation() + semicolonToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return leftValue.representation() + " " + assignToken.representation() + " " + getIntToken.representation()
                + leftParenthesisToken.representation() + rightParenthesisToken.representation()
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
