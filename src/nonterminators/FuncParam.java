package nonterminators;

import error.ErrorChecker;
import error.ErrorHandler;
import error.FatalErrorException;
import foundation.BracketWith;
import foundation.RepresentationBuilder;
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

public record FuncParam(
        BasicType basicType,
        IdentifierToken identifierToken,
        Optional<LeftBracketToken> leftBracketToken,
        Optional<RightBracketToken> rightBracketToken,
        List<BracketWith<ConstExpression>> bracketWithConstExpressionList
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (!bracketWithConstExpressionList.isEmpty()) {
            final var lastIndex = bracketWithConstExpressionList.size() - 1;
            final var lastItem = bracketWithConstExpressionList.get(lastIndex);
            if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
            return lastItem.entity().lastTerminator();
        }
        if (rightBracketToken.isPresent()) return rightBracketToken.get();
        return identifierToken;
    }

    @Override
    public String detailedRepresentation() {
        return basicType.detailedRepresentation()
                + identifierToken.detailedRepresentation()
                + leftBracketToken.map(LeftBracketToken::detailedRepresentation).orElse("")
                + rightBracketToken.map(RightBracketToken::detailedRepresentation).orElse("")
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithConstExpressionList)
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + identifierToken.representation()
                + leftBracketToken.map(LeftBracketToken::representation).orElse("")
                + rightBracketToken.map(RightBracketToken::representation).orElse("")
                + RepresentationBuilder.bracketWithNonTerminatorRepresentation(bracketWithConstExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<FuncFParam>";
    }

    @Override
    public String toString() {
        return "FuncParam{" +
                "basicType=" + basicType +
                ", identifierToken=" + identifierToken +
                ", leftBracketToken=" + leftBracketToken +
                ", rightBracketToken=" + rightBracketToken +
                ", bracketWithConstExpressionList=" + bracketWithConstExpressionList +
                '}';
    }

    public FunctionParameterSymbol generateParameterSymbol(
            SymbolManager symbolManager,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        ErrorChecker.checkRedefinedIdentifier(symbolManager, errorHandler, identifierToken);
        final var identifier = identifierToken.identifier();
        final var dimensionSizes = new ArrayList<Integer>();
        final boolean isArrayPointer;
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
        final var metadata = new FunctionParameterMetadata(
                isArrayPointer,
                basicType,
                dimensionSizes,
                symbolManager.activeRecordOffset()
        );
        return new FunctionParameterSymbol(identifier, metadata);
    }
}
