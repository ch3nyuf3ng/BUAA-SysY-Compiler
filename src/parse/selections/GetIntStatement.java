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

        parse: {
            final LeftValue leftValue;
            final var optionalLeftValue = LeftValue.parse(lexer);
            if (optionalLeftValue.isPresent()) {
                leftValue = optionalLeftValue.get();
            } else {
                break parse;
            }

            final AssignToken assignToken;
            final var optionalAssignToken = lexer.currentToken()
                    .filter(t -> t instanceof AssignToken);
            if (optionalAssignToken.isPresent()) {
                assignToken = (AssignToken) optionalAssignToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final GetIntToken getIntToken;
            final var optionalGetIntToken = lexer.currentToken()
                    .filter(t -> t instanceof GetIntToken);
            if (optionalGetIntToken.isPresent()) {
                getIntToken = (GetIntToken) optionalGetIntToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final LeftParenthesisToken leftParenthesisToken;
            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken);
            if (optionalLeftParenthesisToken.isPresent()) {
                leftParenthesisToken = (LeftParenthesisToken) optionalLeftParenthesisToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final RightParenthesisToken rightParenthesisToken;
            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken);
            if (optionalRightParenthesisToken.isPresent()) {
                rightParenthesisToken = (RightParenthesisToken) optionalRightParenthesisToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final SemicolonToken semicolonToken;
            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken);
            if (optionalSemicolonToken.isPresent()) {
                semicolonToken = (SemicolonToken) optionalSemicolonToken.get();
                lexer.consumeToken();
            } else {
                break parse;
            }

            final var result = new GetIntStatement(
                    leftValue,
                    assignToken,
                    getIntToken,
                    leftParenthesisToken,
                    rightParenthesisToken,
                    semicolonToken
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
        return leftValue.detailedRepresentation()
                + assignToken.detailedRepresentation()
                + getIntToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation()
                + semicolonToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return leftValue.representation() + " "
                + assignToken.representation() + " "
                + getIntToken.representation()
                + leftParenthesisToken.representation()
                + rightParenthesisToken.representation()
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
