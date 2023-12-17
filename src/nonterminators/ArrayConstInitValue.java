package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.ConstInitValueType;
import pcode.code.MemAddZeros;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableSymbol;
import terminators.CommaToken;
import terminators.LeftBraceToken;
import terminators.RightBraceToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ArrayConstInitValue(
        LeftBraceToken leftBraceToken,
        Optional<ConstInitValue> firstInitValue,
        List<Pair<CommaToken, ConstInitValue>> commaWithInitValueList,
        RightBraceToken rightBraceToken
) implements ConstInitValueType {
    @Override
    public TokenType lastTerminator() {
        return rightBraceToken;
    }

    @Override
    public String detailedRepresentation() {
        return firstInitValue.map(initValue -> leftBraceToken.detailedRepresentation()
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        initValue, commaWithInitValueList
                  )
                + rightBraceToken.detailedRepresentation()
                ).orElseGet(() ->
                leftBraceToken.detailedRepresentation()
                + rightBraceToken.detailedRepresentation()
        );
    }

    @Override
    public String representation() {
        return firstInitValue.map(initValue -> leftBraceToken.representation()
                + RepresentationBuilder.binaryOperatorExpressionRepresentation( initValue, commaWithInitValueList)
                + rightBraceToken.representation()
                ).orElseGet(() ->
                leftBraceToken.representation()
                + rightBraceToken.representation()
        );
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public String toString() {
        return "ArrayConstInitValue{" +
                "leftBraceToken=" + leftBraceToken +
                ", firstInitValue=" + firstInitValue +
                ", commaWithInitValueList=" + commaWithInitValueList +
                ", rightBraceToken=" + rightBraceToken +
                '}';
    }

    public List<Integer> precalculateValue(SymbolManager symbolManager, int totalSize) {
        final var values = new ArrayList<Integer>();
        if (firstInitValue.isEmpty()) {
            for (var i = 0; i < totalSize; i += 1) {
                values.add(0);
            }
            return values;
        }
        values.addAll(firstInitValue.get().precalculateValue(symbolManager, 0));
        for (var otherInitValue : commaWithInitValueList) {
            final var valInitValue = otherInitValue.second();
            values.addAll(valInitValue.precalculateValue(symbolManager, 0));
        }
        return values;
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            VariableSymbol variableSymbol,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (firstInitValue.isPresent()) {
            firstInitValue.get().generatePcode(symbolManager, pcodeList, variableSymbol, errorHandler);
            for (final var commaWithInitValue : commaWithInitValueList) {
                final var initValue = commaWithInitValue.second();
                initValue.generatePcode(symbolManager, pcodeList, variableSymbol, errorHandler);
            }
        } else {
            final var totalSize = variableSymbol.metadata().totalSize();
            pcodeList.add(new MemAddZeros(totalSize));
        }
    }
}
