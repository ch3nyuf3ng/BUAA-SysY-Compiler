package nonterminators;

import error.ErrorHandler;
import error.errors.MissingReturnError;
import foundation.Logger;
import foundation.Helpers;
import foundation.typing.IntType;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.DebugPcode;
import pcode.code.Label;
import pcode.protocols.PcodeType;
import symbol.FunctionMetadata;
import symbol.FunctionSymbol;
import symbol.SymbolManager;
import terminators.IntToken;
import terminators.LeftParenthesisToken;
import terminators.MainToken;
import terminators.RightParenthesisToken;
import terminators.protocols.TokenType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record MainFuncDefinition(
        IntToken intToken,
        MainToken mainToken,
        LeftParenthesisToken leftParenthesisToken,
        Optional<RightParenthesisToken> rightParenthesisToken,
        Block block
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return intToken.detailedRepresentation() + mainToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
                + block.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return intToken.representation() + " "
                + mainToken.representation()
                + leftParenthesisToken.representation()
                + rightParenthesisToken.map(RightParenthesisToken::representation).orElse("") + " "
                + block.representation();
    }

    @Override
    public String categoryCode() {
        return "<MainFuncDef>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        final var mainFuncSymbol = new FunctionSymbol(
                "main", new FunctionMetadata(new IntType(), Collections.emptyList())
        );
        symbolManager.addSymbol(mainFuncSymbol);
        symbolManager.setCurrentDefiningFunction(mainFuncSymbol);
        if (Logger.LogEnabled) {
            Logger.debug("Added Main Func Symbol: " + mainFuncSymbol, Logger.Category.SYMBOL);
        }
        if (DebugPcode.Enable) {
            pcodeList.add(new DebugPcode("Main: " + representation()));
        }
        pcodeList.add(new Label("#main_start"));
        symbolManager.createSymbolTable();
        block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        symbolManager.tracebackSymbolTable();
        if (block.lastItemIsNotReturn()) {
            final var rightBrace = block.rightBraceToken();
            final var error = new MissingReturnError(Helpers.lineNumberOf(rightBrace));
            errorHandler.reportError(error);
        }
        pcodeList.add(new Label("#main_end"));
        symbolManager.delCurrentDefiningFunction();
    }
}
