package nonterminators;

import error.ErrorChecker;
import error.ErrorHandler;
import error.FatalErrorException;
import error.errors.AssignToConstError;
import foundation.ArrayPointer;
import foundation.BracketWith;
import foundation.RepresentationBuilder;
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

import java.util.ArrayList;
import java.util.List;

public record LeftValue(
        IdentifierToken identifierToken,
        List<BracketWith<Expression>> bracketWithExpressionList
) implements PrimaryExpressionType, Precalculable {
    @Override
    public TokenType lastTerminator() {
        if (bracketWithExpressionList.isEmpty()) return identifierToken;
        final var lastIndex = bracketWithExpressionList.size() - 1;
        final var lastItem = bracketWithExpressionList.get(lastIndex);
        if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
        return lastItem.entity().lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithExpressionList)
                + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + RepresentationBuilder.bracketWithNonTerminatorRepresentation(bracketWithExpressionList);
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
    public int calculateToInt(SymbolManager symbolManager) {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isPresent() && possibleSymbol.get() instanceof VariableSymbol variableSymbol) {
            final var possiblePrecalculatedValue = variableSymbol.precalculatedValue();
            if (possiblePrecalculatedValue.isPresent()) {
                final var precalculatedValue = possiblePrecalculatedValue.get();
                if (bracketWithExpressionList.isEmpty()) {
                    return precalculatedValue.get(0);
                } else {
                    final var dimensionSizes = new ArrayList<>(variableSymbol.metadata().dimensionSizes());
                    for (var i = dimensionSizes.size() - 2; i >= 0; i -= 1) {
                        dimensionSizes.set(i, dimensionSizes.get(i) * dimensionSizes.get(i + 1));
                    }
                    var expandedIndex = 0;
                    for (var i = 0; i < dimensionSizes.size(); i += 1) {
                        final var dimensionIndex = bracketWithExpressionList.get(i).entity().calculateToInt(symbolManager);
                        expandedIndex += dimensionIndex * dimensionSizes.get(i);
                    }
                    return precalculatedValue.get(expandedIndex);
                }
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            boolean isForStoringValue,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        ErrorChecker.checkUndefiniedIdentifier(symbolManager, errorHandler, identifierToken);
        final var symbol = getSymbol(symbolManager);
        final var indexesCount = bracketWithExpressionList.size();
        final var isCalculatedResultArrayPointer = isCalculatedResultArrayPointer(symbolManager);
        final List<Integer> dimensionSizes;
        final List<Integer> dimensionOffsets;
        final int depth;
        final int addr;
        final boolean isFunctionArrayPointerArg;
        if (symbol instanceof VariableSymbol variableSymbol) {
            if (variableSymbol.metadata().isConstant() && isForStoringValue) {
                final var error = new AssignToConstError(identifierToken.endingPosition().lineNumber());
                errorHandler.reportFatalError(error);
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
            throw new RuntimeException();
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

    public boolean isCalculatedResultArrayPointer(SymbolManager symbolManager) {
        final var symbol = getSymbol(symbolManager);
        final var indexesCount = bracketWithExpressionList.size();
        if (symbol instanceof VariableSymbol variableSymbol) {
            return variableSymbol.metadata().isArray()
                    && indexesCount < variableSymbol.metadata().dimensionSizes().size();
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            return functionParameterSymbol.metadata().isArrayPointer()
                    && indexesCount < functionParameterSymbol.metadata().dimensionSizes().size();
        } else {
            throw new RuntimeException();
        }
    }

    public ArrayPointer arrayPointerType(SymbolManager symbolManager) {
        final var symbol = getSymbol(symbolManager);
        if (symbol instanceof VariableSymbol variableSymbol) {
            final var basicType = variableSymbol.metadata().basicType();
            final var level = variableSymbol.metadata().dimensionSizes().size() - bracketWithExpressionList.size();
            return new ArrayPointer(basicType, level);
        } else if (symbol instanceof FunctionParameterSymbol functionParameterSymbol) {
            final var basicType = functionParameterSymbol.metadata().basicType();
            final var level = functionParameterSymbol.metadata().dimensionSizes().size() - bracketWithExpressionList.size();
            return new ArrayPointer(basicType, level);
        } else {
            throw new RuntimeException();
        }
    }

    private SymbolType getSymbol(SymbolManager symbolManager) {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty()) {
            throw new RuntimeException();
        }
        return possibleSymbol.get();
    }
}