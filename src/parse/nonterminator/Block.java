package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftBraceToken;
import lex.token.RightBraceToken;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Block implements SelectionType {
    private final LeftBraceToken leftBraceToken;
    private final List<BlockItem> blockItemList;
    private final RightBraceToken rightBraceToken;

    public Block(LeftBraceToken leftBraceToken, List<BlockItem> blockItemList, RightBraceToken rightBraceToken) {
        this.leftBraceToken = leftBraceToken;
        this.blockItemList = blockItemList;
        this.rightBraceToken = rightBraceToken;
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(LeftBraceToken.class);
    }

    public static Optional<Block> parse(LexerType lexer) {
        Logger.info("Matching <Block>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final List<BlockItem> blockItemList = new ArrayList<>();
            while (BlockItem.isMatchedBeginningTokens(lexer)) {
                final var blockItem = BlockItem.parse(lexer);
                if (blockItem.isEmpty()) break;
                blockItemList.add(blockItem.get());
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new Block(leftBraceToken.get(), blockItemList, rightBraceToken.get());
            Logger.info("Matched <Block>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <Block>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder().append(leftBraceToken.detailedRepresentation());
        blockItemList.forEach(i -> stringBuilder.append(i.detailedRepresentation()));
        stringBuilder.append(rightBraceToken.detailedRepresentation()).append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        if (blockItemList.isEmpty()) return "{}";
        final var stringBuilder = new StringBuilder().append(leftBraceToken.representation()).append('\n');
        blockItemList.forEach(i -> stringBuilder.append(i.representation().replaceAll("(?m)^", "    ")).append('\n'));
        stringBuilder.append(rightBraceToken.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<Block>";
    }

    @Override
    public TokenType lastTerminator() {
        return null;
    }
}
