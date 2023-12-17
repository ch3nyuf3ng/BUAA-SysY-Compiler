package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.ConstInitValueType;
import nonterminators.protocols.NonTerminatorType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import symbol.VariableSymbol;
import terminators.protocols.TokenType;

import java.util.List;

public record ConstInitValue(
        ConstInitValueType constInitValue
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return constInitValue.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return constInitValue.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return constInitValue.representation();
    }

    @Override
    public String categoryCode() {
        return "<ConstInitVal>";
    }

    @Override
    public String toString() {
        return "ConstInitValue{" +
                "constInitValue=" + constInitValue +
                '}';
    }

    public List<Integer> precalculateValue(SymbolManager symbolManager, int totalSize) {
        if (constInitValue instanceof ArrayConstInitValue arrayConstInitValue) {
            return arrayConstInitValue.precalculateValue(symbolManager, totalSize);
        } else if (constInitValue instanceof ScalarConstInitValue scalarConstInitValue) {
            return scalarConstInitValue.precalculateValue(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            VariableSymbol variableSymbol,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (constInitValue instanceof ScalarConstInitValue scalarConstInitValue) {
            scalarConstInitValue.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (constInitValue instanceof ArrayConstInitValue arrayConstInitValue) {
            arrayConstInitValue.generatePcode(symbolManager, pcodeList, variableSymbol, errorHandler);
        } else {
            throw new RuntimeException();
        }
    }
}
