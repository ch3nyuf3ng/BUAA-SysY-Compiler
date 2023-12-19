package nonterminators;

import error.ErrorHandler;
import error.exceptions.AssignToConstantException;
import error.exceptions.IdentifierUndefineException;
import foundation.BracketWith;
import foundation.Helpers;
import foundation.ReprBuilder;
import foundation.protocols.EvaluationType;
import foundation.typing.ArrayPointerType;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.PrimaryExpressionType;
import pcode.code.LoadAddress;
import pcode.code.LoadImmediate;
import pcode.code.LoadValue;
import pcode.code.Operate;
import pcode.protocols.PcodeType;
import symbol.FunctionParameterSymbol;
import symbol.SymbolManager;
import symbol.VariableSymbol;
import symbol.protocols.SymbolType;
import terminators.IdentifierToken;
import terminators.protocols.TokenType;

import java.util.List;

public record LeftValue(
        IdentifierToken identifierToken,
        List<BracketWith<Expression>> bracketWithExpressionList
) implements PrimaryExpressionType, Precalculable {
    @Override
    public TokenType lastTerminator() {
        if (bracketWithExpressionList.isEmpty()) {
            return identifierToken;
        }
        final var lastIndex = bracketWithExpressionList.size() - 1;
        final var lastItem = bracketWithExpressionList.get(lastIndex);
        if (lastItem.rightBracketToken().isPresent()) {
            return lastItem.rightBracketToken().get();
        }
        return lastItem.entity().lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + ReprBuilder.bracketWithNonTerminatorDetailedRepr(bracketWithExpressionList)
                + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + ReprBuilder.bracketWithNonTerminatorRepr(bracketWithExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<LVal>";
    }

    @Override
    public String toString() {
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty()) {
            throw new IdentifierUndefineException(identifierToken.lineNumber());
        }
        if (possibleSymbol.get() instanceof VariableSymbol variableSymbol) {
            final var precalculatedValue = variableSymbol.precalculatedValue();
            final var dimensionOffsets = variableSymbol.metadata().dimensionOffsets();
            var expandedIndex = 0;
            for (var i = 0; i < dimensionOffsets.size(); i += 1) {
                final var dimensionExpression = bracketWithExpressionList.get(i).entity();
                final var dimensionIndex = dimensionExpression.calculateToInt(symbolManager);
                expandedIndex += dimensionIndex * dimensionOffsets.get(i);
            }
            return precalculatedValue.get(expandedIndex);
        }
        throw new RuntimeException();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList,
            boolean isForStoringValue, ErrorHandler errorHandler
    ) throws IdentifierUndefineException, AssignToConstantException {
        final var symbol = getSymbol(symbolManager);
        final var indexesCount = bracketWithExpressionList.size();
        final var isCalculatedResultArrayPointer = isArrayPointerType(symbolManager);
        final List<Integer> dimensionSizes;
        final List<Integer> dimensionOffsets;
        final int depth;
        final int addr;
        final boolean isFunctionArrayPointerArg;
        if (symbol instanceof VariableSymbol variableSymbol) {
            if (variableSymbol.metadata().isConstant() && isForStoringValue) {
                throw new AssignToConstantException(Helpers.lineNumberOf(identifierToken));
            }
            isFunctionArrayPointerArg = false;
            dimensionSizes = variableSymbol.metadata().dimensionSizes();
            dimensionOffsets = variableSymbol.metadata().dimensionOffsets();
            depth = variableSymbol.metadata().depth();
            addr = variableSymbol.metadata().activeRecordOffset();
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            isFunctionArrayPointerArg = functionParameterSymbol.metadata().isArrayPointer();
            dimensionSizes = functionParameterSymbol.metadata().dimensionSizes();
            dimensionOffsets = functionParameterSymbol.metadata().dimensionOffsets();
            depth = 1;
            addr = functionParameterSymbol.metadata().activeRecordOffset();
        } else {
            throw new RuntimeException("Unknown implementation of SymbolType.");
        }
        if (indexesCount > dimensionSizes.size()) {
            throw new RuntimeException();
        }
        final var level = symbolManager.currentDepth() - depth; // The depth for finding the memory location.

        // Generating dynamic offset.
        pcodeList.add(new LoadImmediate(0));
        // Flatend offset init: 0
        if (indexesCount > 0) {
            // + expression_result * dimension_offset
            for (var i = 0; i < indexesCount; i += 1) {
                final var expression = bracketWithExpressionList.get(i).entity();
                expression.generatePcode(symbolManager, pcodeList, errorHandler);
                final var dimensionOffset = dimensionOffsets.get(i);
                pcodeList.add(new LoadImmediate(dimensionOffset));
                pcodeList.add(new Operate(Operate.Opcode.MULTIPLY));
                pcodeList.add(new Operate(Operate.Opcode.PLUS));
            }
        }

        if (isFunctionArrayPointerArg) {
            pcodeList.add(new LoadImmediate(0));
            pcodeList.add(new LoadValue(level, addr));
            pcodeList.add(new Operate(Operate.Opcode.PLUS));
            if (!isCalculatedResultArrayPointer && !isForStoringValue) {
                pcodeList.add(new LoadValue(-1, 0));
            }
        } else {
            if (isCalculatedResultArrayPointer || isForStoringValue) {
                pcodeList.add(new LoadAddress(level, addr));
            } else {
                pcodeList.add(new LoadValue(level, addr));
            }
        }
    }

    private boolean isArrayPointerType(SymbolManager symbolManager) throws IdentifierUndefineException {
        final var symbol = getSymbol(symbolManager);
        final var indexesCount = bracketWithExpressionList.size();
        if (symbol instanceof VariableSymbol variableSymbol) {
            return variableSymbol.metadata().isArray() && indexesCount < variableSymbol.metadata().dimensionSizes().size();
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            return functionParameterSymbol.metadata().isArrayPointer() && indexesCount < functionParameterSymbol.metadata().dimensionSizes().size();
        } else {
            throw new IdentifierUndefineException(identifierToken.lineNumber());
        }
    }

    private ArrayPointerType arrayPointerType(SymbolManager symbolManager) throws IdentifierUndefineException {
        final var symbol = getSymbol(symbolManager);
        if (symbol instanceof VariableSymbol variableSymbol) {
            final var basicType = variableSymbol.metadata().evaluationType();
            final var level = variableSymbol.metadata().dimensionSizes().size() - bracketWithExpressionList.size();
            return new ArrayPointerType(basicType, level);
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            final EvaluationType basicType;
            final var parameterEvaluationType = functionParameterSymbol.metadata().evaluationType();
            if (parameterEvaluationType instanceof ArrayPointerType arrayPointerType) {
                basicType = arrayPointerType.evaluationType();
            } else {
                basicType = parameterEvaluationType;
            }
            final var level = functionParameterSymbol.metadata().dimensionSizes().size() - bracketWithExpressionList.size();
            return new ArrayPointerType(basicType, level);
        } else {
            throw new IdentifierUndefineException(identifierToken.lineNumber());
        }
    }

    private SymbolType getSymbol(SymbolManager symbolManager) throws IdentifierUndefineException {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty()) {
            throw new IdentifierUndefineException(identifierToken.lineNumber());
        }
        return possibleSymbol.get();
    }

    public EvaluationType evaluationType(SymbolManager symbolManager) throws IdentifierUndefineException {
        if (isArrayPointerType(symbolManager)) {
            return arrayPointerType(symbolManager);
        }
        final var symbol = getSymbol(symbolManager);
        if (symbol instanceof VariableSymbol variableSymbol) {
            return variableSymbol.metadata().evaluationType();
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            return functionParameterSymbol.metadata().evaluationType();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}