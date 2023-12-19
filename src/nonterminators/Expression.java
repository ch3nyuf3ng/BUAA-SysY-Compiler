package nonterminators;

import error.ErrorHandler;
import error.exceptions.IdentifierUndefineException;
import foundation.protocols.EvaluationType;
import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.Precalculable;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record Expression(AdditiveExpression additiveExpression) implements NonTerminatorType, Precalculable {
    @Override
    public TokenType lastTerminator() {
        return additiveExpression.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return additiveExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return additiveExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<Exp>";
    }

    @Override
    public String toString() {
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException {
        return additiveExpression.calculateToInt(symbolManager);
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        additiveExpression.generatePcode(symbolManager, pcodeList, errorHandler);
    }

//    public boolean isArrayPointer(SymbolManager symbolManager) throws IdentifierUndefineException {
//        return additiveExpression.isArrayPointer(symbolManager);
//    }
//
//    public ArrayPointerType arrayPointerType(SymbolManager symbolManager) throws IdentifierUndefineException {
//        return additiveExpression.arrayPointerType(symbolManager);
//    }

    public EvaluationType evaluationType(SymbolManager symbolManager) throws IdentifierUndefineException {
        return additiveExpression().evaluationType(symbolManager);
    }
}
