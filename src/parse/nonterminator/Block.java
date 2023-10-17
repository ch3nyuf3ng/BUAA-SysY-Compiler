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
    private final List<BlockItem> blockItems;
    private final RightBraceToken rightBraceToken;

    public Block(LeftBraceToken leftBraceToken, List<BlockItem> blockItems, RightBraceToken rightBraceToken) {
        this.leftBraceToken = leftBraceToken;
        this.blockItems = blockItems;
        this.rightBraceToken = rightBraceToken;
    }

    public static Optional<Block> parse(LexerType lexer) {
        Logger.info("Matching <Block>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalLeftBraceToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftBraceToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftBraceToken) t;
                    });
            if (optionalLeftBraceToken.isEmpty()) break parse;
            final var leftBraceToken = optionalLeftBraceToken.get();

            final List<BlockItem> blockItems = new ArrayList<>();
            while (true) {
                final var optionalBlockItem = BlockItem.parse(lexer);
                if (optionalBlockItem.isEmpty()) break;
                final BlockItem blockItem = optionalBlockItem.get();
                blockItems.add(blockItem);
            }

            final var optionalRightBraceToken = lexer.currentToken()
                    .filter(t -> t instanceof RightBraceToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightBraceToken) t;
                    });
            if (optionalRightBraceToken.isEmpty()) break parse;
            final var rightBraceToken = optionalRightBraceToken.get();

            final var result = new Block(leftBraceToken, blockItems, rightBraceToken);
            Logger.info("Matched <Block>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <Block>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(leftBraceToken.detailedRepresentation());
        blockItems.forEach(i -> stringBuilder.append(i.detailedRepresentation()));
        stringBuilder
                .append(rightBraceToken.detailedRepresentation())
                .append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        if (blockItems.isEmpty()) return "{}";
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(leftBraceToken.representation()).append('\n');
        blockItems.forEach(i -> stringBuilder
                .append(i.representation().replaceAll("(?m)^", "    ")).append('\n')
        );
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
