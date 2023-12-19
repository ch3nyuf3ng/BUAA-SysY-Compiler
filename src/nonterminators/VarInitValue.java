package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.VarInitValueType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableSymbol;
import terminators.protocols.TokenType;

import java.util.List;

public record VarInitValue(
        VarInitValueType varInitVal
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return varInitVal.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return varInitVal.representation();
    }

    @Override
    public String categoryCode() {
        return "<InitVal>";
    }

    @Override
    public TokenType lastTerminator() {
        return varInitVal.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList,
            VariableSymbol variableSymbol, ErrorHandler errorHandler
    ) {
        if (varInitVal instanceof ScalarVarInitValue scalarVarInitValue) {
            scalarVarInitValue.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (varInitVal instanceof ArrayVarInitValue arrayVarInitValue) {
            arrayVarInitValue.generatePcode(symbolManager, pcodeList, variableSymbol, errorHandler);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
