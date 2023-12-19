package nonterminators;

import error.ErrorHandler;
import error.errors.MissingReturnError;
import error.exceptions.IdentifierRedefineException;
import error.exceptions.IdentifierUndefineException;
import foundation.Logger;
import foundation.Helpers;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.DebugPcode;
import pcode.code.Label;
import pcode.code.ReturnFunction;
import pcode.protocols.PcodeType;
import symbol.FunctionMetadata;
import symbol.FunctionParameterSymbol;
import symbol.FunctionSymbol;
import symbol.SymbolManager;
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
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws IdentifierRedefineException {
        if (DebugPcode.Enable) {
            pcodeList.add(new DebugPcode("Function definition: " + representation()));
        }
        final var identifier = identifierToken.identifier();
        final var possibleSameNameSameLevelSymbol = symbolManager.findSymbol(identifier, false);
        if (possibleSameNameSameLevelSymbol.isPresent()) {
            throw new IdentifierRedefineException(Helpers.lineNumberOf(identifierToken));
        }
        final var parameters = new ArrayList<FunctionParameterSymbol>();
        final var funcLabel = "#" + identifierToken.identifier();
        pcodeList.add(new Label(funcLabel + "_start"));
        final var metadata = new FunctionMetadata(funcType.evaluationType(), parameters);
        final var functionSymbol = new FunctionSymbol(identifier, metadata);
        symbolManager.addSymbol(functionSymbol);
        symbolManager.setCurrentDefiningFunction(functionSymbol);
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Added Function Symbol into Table" + symbolManager.innerSymbolTableIndex() + ": " + functionSymbol,
                    Logger.Category.SYMBOL
            );
        }
        symbolManager.createSymbolTable();
        if (funcParaList.isPresent()) {
            final FunctionParameterSymbol firstParameter;
            try {
                firstParameter = funcParaList.get().firstFuncParam().generateParameterSymbol(symbolManager);
                symbolManager.addSymbol(firstParameter);
                parameters.add(firstParameter);
            } catch (IdentifierUndefineException e) {
                errorHandler.reportError(e);
            }
            for (var commaWithFuncParam : funcParaList.get().commaWithFuncParamList()) {
                try {
                    final var funcParam = commaWithFuncParam.second();
                    final var parameter = funcParam.generateParameterSymbol(symbolManager);
                    symbolManager.addSymbol(parameter);
                    parameters.add(parameter);
                } catch (IdentifierRedefineException | IdentifierUndefineException e) {
                    errorHandler.reportError(e);
                }
            }
            if (Logger.LogEnabled) {
                for (var functionParameterSymbol : parameters) {
                    Logger.debug(
                            "Added Function Parameter Symbol into Table"
                                    + symbolManager.innerSymbolTableIndex() + ": "
                                    + functionParameterSymbol,
                            Logger.Category.SYMBOL
                    );
                }
            }
        }
        block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        if (block.lastItemIsNotReturn() && funcType.funcTypeToken() instanceof VoidToken) {
            pcodeList.add(new ReturnFunction(false));
        } else if (block.lastItemIsNotReturn() && !(funcType.funcTypeToken() instanceof VoidToken)) {
            final var rightBrace = block.rightBraceToken();
            final var error = new MissingReturnError(Helpers.lineNumberOf(rightBrace));
            errorHandler.reportError(error);
        }
        pcodeList.add(new Label(funcLabel + "_end"));
        symbolManager.tracebackSymbolTable();
        symbolManager.delCurrentDefiningFunction();
    }
}
