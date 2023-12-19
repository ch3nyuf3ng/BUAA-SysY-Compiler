package nonterminators;

import error.ErrorHandler;
import foundation.Pair;
import foundation.ReprBuilder;
import nonterminators.protocols.VarInitValueType;
import pcode.code.MemAddZeros;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableSymbol;
import terminators.CommaToken;
import terminators.LeftBraceToken;
import terminators.RightBraceToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ArrayVarInitValue(
        LeftBraceToken leftBraceToken,
        Optional<VarInitValue> firstInitValue,
        List<Pair<CommaToken, VarInitValue>> commaWithInitValueList,
        RightBraceToken rightBraceToken
) implements VarInitValueType {
    @Override
    public String detailedRepresentation() {
        return firstInitValue.map(initValue ->
                leftBraceToken.detailedRepresentation()
                        + ReprBuilder.binaryOpExpDetailedRepr(initValue, commaWithInitValueList)
                        + rightBraceToken.detailedRepresentation()
        ).orElseGet(() -> leftBraceToken.detailedRepresentation() + rightBraceToken.detailedRepresentation());
    }

    @Override
    public String representation() {
        return firstInitValue.map(varInitValue ->
                leftBraceToken.representation()
                        + ReprBuilder.binaryOpExRepr(varInitValue, commaWithInitValueList)
                        + rightBraceToken.representation()
        ).orElseGet(() -> leftBraceToken.representation() + rightBraceToken.representation());
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return rightBraceToken;
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList,
            VariableSymbol variableSymbol, ErrorHandler errorHandler
    ) {
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