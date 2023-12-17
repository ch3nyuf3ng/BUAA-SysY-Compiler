package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.ArrayPointer;
import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.NonTerminatorType;
import nonterminators.protocols.Precalculable;
import pcode.code.Operate;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.MinusToken;
import terminators.PlusToken;
import terminators.protocols.AdditiveTokenType;
import terminators.protocols.TokenType;

import java.util.List;

public record AdditiveExpression(
        MultiplicativeExpression firstExpression,
        List<Pair<AdditiveTokenType, MultiplicativeExpression>> operatorWithExpressionList
) implements NonTerminatorType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatorExpressionWithCategoryCodeForEachPairDetailedRepresentation(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(
                firstExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<AddExp>";
    }

    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String toString() {
        return "AdditiveExpression{" +
                "firstExpression=" + firstExpression +
                ", operatorWithExpressionList=" + operatorWithExpressionList +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        var sum = firstExpression.calculateToInt(symbolManager);
        for (var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            if (operator instanceof PlusToken) {
                sum += expression.calculateToInt(symbolManager);
            } else {
                sum -= expression.calculateToInt(symbolManager);
            }
        }
        return sum;
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        firstExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        for (var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            expression.generatePcode(symbolManager, pcodeList, errorHandler);
            if (operator instanceof PlusToken) {
                pcodeList.add(new Operate(Operate.Opcode.PLUS));
            } else if (operator instanceof MinusToken) {
                pcodeList.add(new Operate(Operate.Opcode.MINUS));
            } else {
                throw new RuntimeException();
            }
        }
    }

    public boolean isArrayPointer(SymbolManager symbolManager) {
        if (operatorWithExpressionList.isEmpty()) {
            return firstExpression.isArrayPointer(symbolManager);
        } else {
            return false;
        }
    }

    public ArrayPointer arrayPointerType(SymbolManager symbolManager) {
        return firstExpression.arrayPointerType(symbolManager);
    }
}
