package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.VarInitValueType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record ScalarVarInitValue(
        Expression expression
) implements VarInitValueType {
    @Override
    public String detailedRepresentation() {
        return expression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return expression.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return expression.lastTerminator();
    }

    @Override
    public String toString() {
        return "ScalarVarInitValue{" +
                "expression=" + expression +
                '}';
    }

    public List<Integer> precalculateValue(SymbolManager symbolManager) {
        return List.of(expression.calculateToInt(symbolManager));
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        expression.generatePcode(symbolManager, pcodeList, errorHandler);
    }
}
