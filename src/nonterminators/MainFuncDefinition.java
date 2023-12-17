package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import error.errors.MissingReturnError;
import foundation.Logger;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.Debug;
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

public record MainFuncDefinition(
        IntToken intToken,
        MainToken mainToken,
        LeftParenthesisToken leftParenthesisToken,
        RightParenthesisToken rightParenthesisToken,
        Block block
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return intToken.detailedRepresentation() + mainToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation() + rightParenthesisToken.detailedRepresentation()
                + block.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return intToken.representation() + " " + mainToken.representation() + leftParenthesisToken.representation()
                + rightParenthesisToken.representation() + " " + block.representation();
    }

    @Override
    public String categoryCode() {
        return "<MainFuncDef>";
    }

    @Override
    public String toString() {
        return "MainFuncDefinition{" +
                "intToken=" + intToken +
                ", mainToken=" + mainToken +
                ", leftParenthesisToken=" + leftParenthesisToken +
                ", rightParenthesisToken=" + rightParenthesisToken +
                ", block=" + block +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        final var mainFuncSymbol = new FunctionSymbol(
                "main",
                new FunctionMetadata(
                        new FuncType(intToken),
                        Collections.emptyList()
                )
        );
        symbolManager.addSymbol(mainFuncSymbol);
        if (Logger.LogEnabled) {
            Logger.debug("Added Main Func Symbol: " + mainFuncSymbol, Logger.Category.SYMBOL);
        }
        if (Debug.Enable) {
            pcodeList.add(new Debug("Main: " + representation()));
        }
        pcodeList.add(new Label("#main_start"));
        symbolManager.createSymbolTable();
        block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        symbolManager.tracebackSymbolTable();
        if (!block.lastItemIsReturn()) {
            final var error = new MissingReturnError(block.rightBraceToken().endingPosition().lineNumber());
            errorHandler.reportFatalError(error);
        }
        pcodeList.add(new Label("#main_end"));
    }
}
