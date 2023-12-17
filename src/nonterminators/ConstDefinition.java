package nonterminators;

import error.ErrorChecker;
import error.ErrorHandler;
import error.FatalErrorException;
import foundation.BracketWith;
import foundation.Logger;
import foundation.RepresentationBuilder;
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
import java.util.Optional;

public record ConstDefinition(
        IdentifierToken identifierToken,
        List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
        AssignToken assignToken,
        ConstInitValue initValue
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithConstExpressionList)
                + assignToken.detailedRepresentation()
                + initValue.detailedRepresentation()
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + RepresentationBuilder.bracketWithNonTerminatorRepresentation(bracketWithConstExpressionList) + ' '
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
        return "ConstDefinition{" +
                "identifierToken=" + identifierToken +
                ", bracketWithConstExpressionList=" + bracketWithConstExpressionList +
                ", assignToken=" + assignToken +
                ", initValue=" + initValue +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            BasicType basicType,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        ErrorChecker.checkRedefinedIdentifier(symbolManager, errorHandler, identifierToken);
        final var identifier = identifierToken.identifier();
        final var isArray = !bracketWithConstExpressionList.isEmpty();
        final var surfacialDimensionSizes = new ArrayList<Integer>();
        for (var bracketWithConstExpression : bracketWithConstExpressionList) {
            final var dimensionSize = bracketWithConstExpression.entity().calculateToInt(symbolManager);
            surfacialDimensionSizes.add(dimensionSize);
        }
        final var activeRecordOffset = symbolManager.activeRecordOffset();
        final var depth = symbolManager.currentDepth();
        final var metadata = new VariableMetadata(true, isArray, basicType, surfacialDimensionSizes, activeRecordOffset, depth);
        final var precalculatedValues = Optional.of(initValue.precalculateValue(symbolManager, metadata.totalSize()));
        final var symbol = new VariableSymbol(identifier, metadata, precalculatedValues);
        symbolManager.addSymbol(symbol);
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Added Constant Symbol into Table"
                            + symbolManager.innerSymbolTableIndex()
                            + ": " + symbol,
                    Logger.Category.SYMBOL
            );
        }
        initValue.generatePcode(symbolManager, pcodeList, symbol, errorHandler);
    }
}
