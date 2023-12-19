package nonterminators;

import error.ErrorHandler;
import error.exceptions.AssignToConstantException;
import error.exceptions.FuncArgNumUnmatchException;
import error.exceptions.FuncArgTypeUnmatchException;
import error.exceptions.IdentifierUndefineException;
import foundation.protocols.EvaluationType;
import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.UnaryExpressionType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record UnaryExpression(UnaryExpressionType unaryExpression) implements NonTerminatorType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return unaryExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return unaryExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<UnaryExp>";
    }

    @Override
    public TokenType lastTerminator() {
        return unaryExpression.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException {
        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            return primaryExpression.calculateToInt(symbolManager);
        } else if (unaryExpression instanceof UnaryOperatedExpression unaryOperatedExpression) {
            return unaryOperatedExpression.calculateToInt(symbolManager);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) throws IdentifierUndefineException, AssignToConstantException,
            FuncArgTypeUnmatchException, FuncArgNumUnmatchException {
        if (unaryExpression instanceof UnaryOperatedExpression unaryOperatedExpression) {
            unaryOperatedExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            primaryExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (unaryExpression instanceof FuncInvocation funcInvocation) {
            funcInvocation.generatePcode(symbolManager, pcodeList, errorHandler);
        }
    }

//    public boolean isArrayPointer(SymbolManager symbolManager) throws IdentifierUndefineException {
//        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
//            return primaryExpression.isArrayPointer(symbolManager);
//        } else {
//            return false;
//        }
//    }
//
//    public ArrayPointerType arrayPointerType(SymbolManager symbolManager) throws IdentifierUndefineException {
//        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
//            return primaryExpression.arrayPointerType(symbolManager);
//        } else {
//            throw new UnsupportedOperationException();
//        }
//    }

    public EvaluationType evaluationType(SymbolManager symbolManager) throws IdentifierUndefineException {
        if (unaryExpression instanceof UnaryOperatedExpression unaryOperatedExpression) {
            return unaryOperatedExpression.unaryExpression().evaluationType(symbolManager);
        } else if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            return primaryExpression.evaluationType(symbolManager);
        } else if (unaryExpression instanceof FuncInvocation funcInvocation) {
            return funcInvocation.evaluationType(symbolManager);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
