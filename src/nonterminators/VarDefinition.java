package nonterminators;

import error.ErrorHandler;
import error.exceptions.IdentifierRedefineException;
import error.exceptions.IdentifierUndefineException;
import foundation.*;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.MemAddZeros;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableMetadata;
import symbol.VariableSymbol;
import terminators.AssignToken;
import terminators.IdentifierToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record VarDefinition(
        IdentifierToken identifierToken,
        List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
        Optional<AssignToken> assignToken,
        Optional<VarInitValue> varInitValue
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + ReprBuilder.bracketWithNonTerminatorDetailedRepr(bracketWithConstExpressionList)
                + assignToken.map(AssignToken::detailedRepresentation).orElse("")
                + varInitValue.map(VarInitValue::detailedRepresentation).orElse("") + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.representation());
        for (final var i : bracketWithConstExpressionList) {
            stringBuilder.append(i.leftBracketToken().representation()).append(i.entity().representation());
            i.rightBracketToken().ifPresent(x -> stringBuilder.append(x.representation()));
        }
        assignToken.ifPresent(t -> stringBuilder.append(' ').append(t.representation()));
        varInitValue.ifPresent(t -> stringBuilder.append(' ').append(t.representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<VarDef>";
    }

    @Override
    public TokenType lastTerminator() {
        if (varInitValue.isPresent()) {
            return varInitValue.get().lastTerminator();
        }
        if (!bracketWithConstExpressionList.isEmpty()) {
            final var lastIndex = bracketWithConstExpressionList.size() - 1;
            final var lastItem = bracketWithConstExpressionList.get(lastIndex);
            if (lastItem.rightBracketToken().isPresent()) {
                return lastItem.rightBracketToken().get();
            }
            return lastItem.entity().lastTerminator();
        }
        return identifierToken;
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
            throw new IdentifierRedefineException(Helpers.lineNumberOf(identifierToken));
        }
        final var isArray = !bracketWithConstExpressionList.isEmpty();
        final var dimensionSizes = new ArrayList<Integer>();
        for (var bracketWithConstExpression : bracketWithConstExpressionList) {
            final var dimensionSize = bracketWithConstExpression.entity().calculateToInt(symbolManager);
            dimensionSizes.add(dimensionSize);
        }
        final var activeRecordOffset = symbolManager.activeRecordOffset();
        final var depth = symbolManager.currentDepth();
        final var metadata = new VariableMetadata(
                false, isArray, basicType.evaluationType(), dimensionSizes, activeRecordOffset, depth
        );
        final var symbol = new VariableSymbol(identifier, metadata, Collections.emptyList());
        symbolManager.addSymbol(symbol);
        if (Logger.LogEnabled) {
            Logger.debug(
                    "Added Variable Symbol into Table" + symbolManager.innerSymbolTableIndex() + ": " + symbol,
                    Logger.Category.SYMBOL
            );
        }
        if (varInitValue.isEmpty()) {
            final int totalSize = symbol.metadata().totalSize();
            pcodeList.add(new MemAddZeros(totalSize));
        } else {
            varInitValue.get().generatePcode(symbolManager, pcodeList, symbol, errorHandler);
        }
    }
}
