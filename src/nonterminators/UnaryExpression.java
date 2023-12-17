package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.ArrayPointer;
import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.UnaryExpressionType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record UnaryExpression(
        UnaryExpressionType unaryExpression
) implements NonTerminatorType, Precalculable {
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
        return "UnaryExpression{" +
                "unaryExpression=" + unaryExpression +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            return primaryExpression.calculateToInt(symbolManager);
        } else if (unaryExpression instanceof UnaryOperatedExpression unaryOperatedExpression) {
            return unaryOperatedExpression.calculateToInt(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (unaryExpression instanceof UnaryOperatedExpression unaryOperatedExpression) {
            unaryOperatedExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            primaryExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (unaryExpression instanceof FuncInvocation funcInvocation) {
            funcInvocation.generatePcode(symbolManager, pcodeList, errorHandler);
        } else {
            throw new RuntimeException();
        }
    }

    public boolean isArrayPointer(SymbolManager symbolManager) {
        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            return primaryExpression.isArrayPointer(symbolManager);
        } else {
            return false;
        }
    }

    public ArrayPointer arrayPointerType(SymbolManager symbolManager) {
        if (unaryExpression instanceof PrimaryExpression primaryExpression) {
            return primaryExpression.arrayPointerType(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }
}
