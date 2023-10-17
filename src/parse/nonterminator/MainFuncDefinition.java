package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IntToken;
import lex.token.LeftParenthesisToken;
import lex.token.MainToken;
import lex.token.RightParenthesisToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class MainFuncDefinition implements NonTerminatorType {
    private final IntToken intToken;
    private final MainToken mainToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final RightParenthesisToken rightParenthesisToken;
    private final Block block;

    private MainFuncDefinition(
            IntToken intToken,
            MainToken mainToken,
            LeftParenthesisToken leftParenthesisToken,
            RightParenthesisToken rightParenthesisToken,
            Block block
    ) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.rightParenthesisToken = rightParenthesisToken;
        this.block = block;
    }

    public static Optional<MainFuncDefinition> parse(LexerType lexer) {
        Logger.info("Matching <MainFuncDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalIntToken = lexer.currentToken()
                    .filter(t -> t instanceof IntToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IntToken) t;
                    });
            if (optionalIntToken.isEmpty()) break parse;
            final var intToken = optionalIntToken.get();

            final var optionalMainToken = lexer.currentToken()
                    .filter(t -> t instanceof MainToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (MainToken) t;
                    });
            if (optionalMainToken.isEmpty()) break parse;
            final var mainToken = optionalMainToken.get();

            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftParenthesisToken) t;
                    });
            if (optionalLeftParenthesisToken.isEmpty()) break parse;
            final var leftParenthesisToken = optionalLeftParenthesisToken.get();

            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightParenthesisToken) t;
                    });
            if (optionalRightParenthesisToken.isEmpty()) break parse;
            final var rightParenthesisToken = optionalRightParenthesisToken.get();

            final var optionalBlock = Block.parse(lexer);
            if (optionalBlock.isEmpty()) break parse;
            final var block = optionalBlock.get();

            final var result = new MainFuncDefinition(
                    intToken,
                    mainToken,
                    leftParenthesisToken,
                    rightParenthesisToken,
                    block
            );
            Logger.info("Matched <MainFuncDefinition>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <MainFuncDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }


    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return intToken.detailedRepresentation()
                + mainToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation()
                + block.detailedRepresentation()
                + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(intToken.representation()).append(" ")
                .append(mainToken.representation())
                .append(leftParenthesisToken.representation())
                .append(rightParenthesisToken.representation()).append(" ")
                .append(block.representation());
        final var lastCharIndex = stringBuilder.length() - 1;
        final var lastChar = stringBuilder.charAt(lastCharIndex);
        if (lastChar == '\n') {
            stringBuilder.deleteCharAt(lastCharIndex);
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<MainFuncDef>";
    }
}
