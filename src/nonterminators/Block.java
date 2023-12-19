package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.StatementType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LeftBraceToken;
import terminators.RightBraceToken;
import terminators.protocols.TokenType;

import java.util.List;

public record Block(
        LeftBraceToken leftBraceToken,
        List<BlockItem> blockItemList,
        RightBraceToken rightBraceToken
) implements StatementType {
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
        return rightBraceToken;
    }

    @Override
    public String toString() {
        return representation();
    }

    public boolean lastItemIsNotReturn() {
        if (blockItemList.isEmpty()) {
            return true;
        }
        final var lastBlockItem = blockItemList.get(blockItemList.size() - 1);
        if (lastBlockItem.blockItem() instanceof Statement statement) {
            return !(statement.statement() instanceof ReturnStatement);
        }
        return true;
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        for (final var blockItem : blockItemList) {
            blockItem.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        }
    }
}
