package nonterminators;

import error.ErrorHandler;
import error.exceptions.IdentifierRedefineException;
import error.exceptions.IdentifierUndefineException;
import foundation.BracketWith;
import foundation.Logger;
import foundation.ReprBuilder;
import nonterminators.protocols.NonTerminatorType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableMetadata;
import symbol.VariableSymbol;
import terminators.AssignToken;
import terminators.IdentifierToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;

public record ConstDefinition(
        IdentifierToken identifierToken,
        List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
        AssignToken assignToken,
        ConstInitValue initValue
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + ReprBuilder.bracketWithNonTerminatorDetailedRepr(bracketWithConstExpressionList)
                + assignToken.detailedRepresentation()
                + initValue.detailedRepresentation()
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + ReprBuilder.bracketWithNonTerminatorRepr(bracketWithConstExpressionList) + ' '
                + assignToken.representation() + ' '
                + initValue.representation();
    }

    @Override
    public String categoryCode() {
        return "<ConstDef>";
    }

    @Override
    public TokenType lastTerminator() {
        return initValue.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, BasicType basicType, ErrorHandler errorHandler
    ) throws IdentifierRedefineException, IdentifierUndefineException {
        final var identifier = identifierToken.identifier();
        final var possibleSameNameSameLevelSymbol = symbolManager.findSymbol(identifier, false);
        if (possibleSameNameSameLevelSymbol.isPresent()) {
            throw new IdentifierRedefineException(identifierToken.lineNumber());
        }
        final var isArray = !bracketWithConstExpressionList.isEmpty();
        final var surfacialDimensionSizes = new ArrayList<Integer>();
        for (var bracketWithConstExpression : bracketWithConstExpressionList) {
            final var dimensionSize = bracketWithConstExpression.entity().calculateToInt(symbolManager);
            surfacialDimensionSizes.add(dimensionSize);
        }
        final var activeRecordOffset = symbolManager.activeRecordOffset();
        final var depth = symbolManager.currentDepth();
        final var metadata = new VariableMetadata(
                true, isArray, basicType.evaluationType(), surfacialDimensionSizes, activeRecordOffset, depth
        );
        final var precalculatedValues = initValue.precalculateValue(symbolManager, metadata.totalSize());
        final var symbol = new VariableSymbol(identifier, metadata, precalculatedValues);
        symbolManager.addSymbol(symbol);
        if (Logger.LogEnabled) {
            Logger.debug("Added Constant Symbol into Table"
                    + symbolManager.innerSymbolTableIndex() + ": " + symbol
                    , Logger.Category.SYMBOL
            );
        }
        initValue.generatePcode(symbolManager, pcodeList, symbol, errorHandler);
    }
}
