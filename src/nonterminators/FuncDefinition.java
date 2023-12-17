package nonterminators;

import error.ErrorChecker;
import error.ErrorHandler;
import error.FatalErrorException;
import error.errors.MissingReturnError;
import foundation.Logger;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.Debug;
import pcode.code.Label;
import pcode.code.ReturnFunction;
import pcode.protocols.PcodeType;
import symbol.*;
import terminators.IdentifierToken;
import terminators.LeftParenthesisToken;
import terminators.RightParenthesisToken;
import terminators.VoidToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FuncDefinition(
        FuncType funcType,
        IdentifierToken identifierToken,
        LeftParenthesisToken leftParenthesisToken,
        Optional<FuncParamList> funcParaList,
        Optional<RightParenthesisToken> rightParenthesisToken,
        Block block
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return funcType.detailedRepresentation()
                + identifierToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + funcParaList.map(FuncParamList::detailedRepresentation).orElse("")
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
                + block.detailedRepresentation()
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return funcType.representation() + ' '
                + identifierToken.representation()
                + leftParenthesisToken.representation()
                + funcParaList.map(FuncParamList::representation).orElse("")
                + rightParenthesisToken.map(RightParenthesisToken::representation).orElse("") + ' '
                + block.representation();
    }

    @Override
    public String categoryCode() {
        return "<FuncDef>";
    }

    @Override
    public String toString() {
        return "FuncDefinition{" +
                "funcType=" + funcType +
                ", identifierToken=" + identifierToken +
                ", leftParenthesisToken=" + leftParenthesisToken +
                ", funcParaList=" + funcParaList +
                ", rightParenthesisToken=" + rightParenthesisToken +
                ", block=" + block +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (Debug.Enable) {
            pcodeList.add(new Debug("Function definition: " + representation()));
        }
        ErrorChecker.checkRedefinedIdentifier(symbolManager, errorHandler, identifierToken);
        final var identifier = identifierToken.identifier();
        final var parameters = new ArrayList<FunctionParameterSymbol>();
        final var funcLabel = "#" + identifierToken.identifier();
        pcodeList.add(new Label(funcLabel + "_start"));
        final var metadata = new FunctionMetadata(funcType, parameters);
        final var functionSymbol = new FunctionSymbol(identifier, metadata);
        symbolManager.addSymbol(functionSymbol);
        symbolManager.setCurrentDefiningFunction(functionSymbol);
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Added Function Symbol into Table"
                            + symbolManager.innerSymbolTableIndex()
                            + ": " + functionSymbol,
                    Logger.Category.SYMBOL
            );
        }
        symbolManager.createSymbolTable();
        if (funcParaList.isPresent()) {
            final var firstParameter = funcParaList.get().firstFuncParam().generateParameterSymbol(symbolManager, errorHandler);
            symbolManager.addSymbol(firstParameter);
            parameters.add(firstParameter);
            for (var commaWithFuncParam : funcParaList.get().commaWithFuncParamList()) {
                final var funcParam = commaWithFuncParam.second();
                final var parameter = funcParam.generateParameterSymbol(symbolManager, errorHandler);
                symbolManager.addSymbol(parameter);
                parameters.add(parameter);
            }
            for (var functionParameterSymbol : parameters) {
                if (Logger.LogEnabled) {
                    Logger.debug(
                            "Added Function Parameter Symbol into Table"
                                    + symbolManager.innerSymbolTableIndex()
                                    + ": " + functionParameterSymbol,
                            Logger.Category.SYMBOL
                    );
                }
            }
        }
        block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        if (!block.lastItemIsReturn() && funcType.funcType() instanceof VoidToken) {
            pcodeList.add(new ReturnFunction(false));
        } else if (!block.lastItemIsReturn() && !(funcType.funcType() instanceof VoidToken)) {
            final var error = new MissingReturnError(block.rightBraceToken().endingPosition().lineNumber());
            errorHandler.reportFatalError(error);
        }
        pcodeList.add(new Label(funcLabel + "_end"));
        symbolManager.tracebackSymbolTable();
        symbolManager.delCurrentDefiningFunction();
    }
}
