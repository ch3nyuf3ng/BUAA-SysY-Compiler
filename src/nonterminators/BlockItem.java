package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.BlockItemType;
import nonterminators.protocols.NonTerminatorType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record BlockItem(
        BlockItemType blockItem
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return blockItem.detailedRepresentation();
    }

    @Override
    public String representation() {
        return blockItem.representation();
    }

    @Override
    public String categoryCode() {
        return "<BlockItem>";
    }

    @Override
    public TokenType lastTerminator() {
        return blockItem.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        if (blockItem instanceof Declaration declaration) {
            declaration.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (blockItem instanceof Statement statement) {
            statement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
