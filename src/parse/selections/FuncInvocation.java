package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftParenthesisToken;
import lex.token.RightParenthesisToken;
import parse.nonterminator.FuncArgList;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class FuncInvocation implements SelectionType {
    final private IdentifierToken identifierToken;
    final private LeftParenthesisToken leftParenthesisToken;
    final private Optional<FuncArgList> optionalFuncArgList;
    final private RightParenthesisToken rightParenthesisToken;

    private FuncInvocation(
            IdentifierToken identifierToken,
            LeftParenthesisToken leftParenthesisToken,
            Optional<FuncArgList> optionalFuncArgList,
            RightParenthesisToken rightParenthesisToken
    ) {
        this.identifierToken = identifierToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.optionalFuncArgList = optionalFuncArgList;
        this.rightParenthesisToken = rightParenthesisToken;
    }

    public static Optional<FuncInvocation> parse(LexerType lexer) {
        Logger.info("Matching <FuncInvocation>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalIdentifierToken = lexer.currentToken()
                    .filter(t -> t instanceof IdentifierToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IdentifierToken) t;
                    });
            if (optionalIdentifierToken.isEmpty()) break parse;
            final var identifierToken = optionalIdentifierToken.get();

            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftParenthesisToken) t;
                    });
            if (optionalLeftParenthesisToken.isEmpty()) break parse;
            final var leftParenthesisToken = optionalLeftParenthesisToken.get();

            final Optional<FuncArgList> optionalFuncArgList = FuncArgList.parse(lexer);

            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightParenthesisToken) t;
                    });
            if (optionalRightParenthesisToken.isEmpty()) break parse;
            final var rightParenthesisToken = optionalRightParenthesisToken.get();

            final var result = new FuncInvocation(
                    identifierToken,
                    leftParenthesisToken,
                    optionalFuncArgList,
                    rightParenthesisToken
            );
            Logger.info("Matched <FuncInvocation>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncInvocation>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + optionalFuncArgList.map(FuncArgList::detailedRepresentation).orElse("")
                + rightParenthesisToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + leftParenthesisToken.representation()
                + optionalFuncArgList.map(FuncArgList::representation).orElse("")
                + rightParenthesisToken.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return rightParenthesisToken;
    }
}
