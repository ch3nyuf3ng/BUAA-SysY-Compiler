package nonterminators;

import error.exceptions.IdentifierRedefineException;
import error.exceptions.IdentifierUndefineException;
import foundation.BracketWith;
import foundation.Helpers;
import foundation.ReprBuilder;
import foundation.protocols.EvaluationType;
import foundation.typing.ArrayPointerType;
import nonterminators.protocols.NonTerminatorType;
import symbol.FunctionParameterMetadata;
import symbol.FunctionParameterSymbol;
import symbol.SymbolManager;
import terminators.IdentifierToken;
import terminators.LeftBracketToken;
import terminators.RightBracketToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FuncParam(BasicType basicType, IdentifierToken identifierToken,
                        Optional<LeftBracketToken> leftBracketToken, Optional<RightBracketToken> rightBracketToken,
                        List<BracketWith<ConstExpression>> bracketWithConstExpressionList) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (!bracketWithConstExpressionList.isEmpty()) {
            final var lastIndex = bracketWithConstExpressionList.size() - 1;
            final var lastItem = bracketWithConstExpressionList.get(lastIndex);
            if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
            return lastItem.entity().lastTerminator();
        }
        if (rightBracketToken.isPresent()) {
            return rightBracketToken.get();
        }
        return identifierToken;
    }

    @Override
    public String detailedRepresentation() {
        return basicType.detailedRepresentation()
                + identifierToken.detailedRepresentation()
                + leftBracketToken.map(LeftBracketToken::detailedRepresentation).orElse("")
                + rightBracketToken.map(RightBracketToken::detailedRepresentation).orElse("")
                + ReprBuilder.bracketWithNonTerminatorDetailedRepr(bracketWithConstExpressionList)
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + identifierToken.representation()
                + leftBracketToken.map(LeftBracketToken::representation).orElse("")
                + rightBracketToken.map(RightBracketToken::representation).orElse("")
                + ReprBuilder.bracketWithNonTerminatorRepr(bracketWithConstExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<FuncFParam>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public FunctionParameterSymbol generateParameterSymbol(
            SymbolManager symbolManager
    ) throws IdentifierRedefineException, IdentifierUndefineException {
        final var identifier = identifierToken.identifier();
        final var possibleSameNameSameLevelSymbol = symbolManager.findSymbol(identifier, false);
        if (possibleSameNameSameLevelSymbol.isPresent()) {
            throw new IdentifierRedefineException(Helpers.lineNumberOf(identifierToken));
        }
        final var dimensionSizes = new ArrayList<Integer>();
        final boolean isArrayPointer;
        final EvaluationType evaluationType;
        if (leftBracketToken().isPresent()) {
            isArrayPointer = true;
            dimensionSizes.add(0);
        } else {
            isArrayPointer = false;
        }
        for (var bracketWithConstExpression : bracketWithConstExpressionList) {
            final var dimensionSize = bracketWithConstExpression.entity().calculateToInt(symbolManager);
            dimensionSizes.add(dimensionSize);
        }
        if (leftBracketToken().isPresent()) {
            evaluationType = new ArrayPointerType(
                    basicType().evaluationType(), dimensionSizes.size(), new ArrayList<>(dimensionSizes)
            );
        } else {
            evaluationType = basicType().evaluationType();
        }
        final var metadata = new FunctionParameterMetadata(
                isArrayPointer, evaluationType, dimensionSizes, symbolManager.activeRecordOffset()
        );
        return new FunctionParameterSymbol(identifier, metadata);
    }
}
