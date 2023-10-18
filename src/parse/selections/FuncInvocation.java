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

    public static boolean isMatchedBeginningTokens(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class)).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    public static Optional<FuncInvocation> parse(LexerType lexer) {
        Logger.info("Matching <FuncInvocation>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var funcArgList = FuncArgList.parse(lexer);

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var result = new FuncInvocation(identifierToken.get(),
                    leftParenthesisToken.get(),
                    funcArgList,
                    rightParenthesisToken.get()
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
