package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.ConstInitValueType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record ScalarConstInitValue(
        ConstExpression constExpression
) implements ConstInitValueType {
    @Override
    public String detailedRepresentation() {
        return constExpression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return constExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return constExpression.lastTerminator();
    }

    @Override
    public String toString() {
        return "ScalarConstInitValue{" +
                "constExpression=" + constExpression +
                '}';
    }

    public List<Integer> precalculateValue(SymbolManager symbolManager) {
        return List.of(constExpression.calculateToInt(symbolManager));
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        constExpression.generatePcode(symbolManager, pcodeList, errorHandler);
    }
}
