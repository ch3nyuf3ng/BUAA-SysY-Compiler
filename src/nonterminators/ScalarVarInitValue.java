package nonterminators;

import error.ErrorHandler;
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
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        expression.generatePcode(symbolManager, pcodeList, errorHandler);
    }
}
