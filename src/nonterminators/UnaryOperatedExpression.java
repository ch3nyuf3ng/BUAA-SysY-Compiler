package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.UnaryExpressionType;
import pcode.code.Operate;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LogicalNotToken;
import terminators.MinusToken;
import terminators.PlusToken;
import terminators.protocols.TokenType;

import java.util.List;

public record UnaryOperatedExpression(
        UnaryOperator unaryOperator,
        UnaryExpression unaryExpression
) implements UnaryExpressionType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return unaryOperator.detailedRepresentation() + unaryExpression.detailedRepresentation();
    }

    @Override
    public String representation() {
        return unaryOperator.representation() + unaryExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return unaryExpression.lastTerminator();
    }

    @Override
    public String toString() {
        return "UnaryOperatedExpression{" +
                "unaryOperator=" + unaryOperator +
                ", unaryExpression=" + unaryExpression +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        if (unaryOperator.operator() instanceof PlusToken) {
            return unaryExpression.calculateToInt(symbolManager);
        } else if (unaryOperator.operator() instanceof MinusToken) {
            return -unaryExpression.calculateToInt(symbolManager);
        } else if (unaryOperator.operator() instanceof LogicalNotToken) {
            return unaryExpression.calculateToInt(symbolManager) > 0 ? 0 : 1;
        } else {
            throw new RuntimeException();
        }
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        unaryExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        if (unaryOperator.operator() instanceof LogicalNotToken) {
            pcodeList.add(new Operate(Operate.Opcode.LOGICAL_NOT));
        } else if (unaryOperator.operator() instanceof MinusToken) {
            pcodeList.add(new Operate(Operate.Opcode.NEGATIVE));
        } else if (unaryOperator.operator() instanceof PlusToken) {
            pcodeList.add(new Operate(Operate.Opcode.POSITIVE));
        } else {
            throw new RuntimeException();
        }
    }
}
