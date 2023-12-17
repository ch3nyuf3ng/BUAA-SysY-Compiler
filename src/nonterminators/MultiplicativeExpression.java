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
import terminators.DivideToken;
import terminators.ModulusToken;
import terminators.MultiplyToken;
import terminators.protocols.MultiplicativeTokenType;
import terminators.protocols.TokenType;

import java.util.List;

public record MultiplicativeExpression(
        UnaryExpression firstExpression,
        List<Pair<MultiplicativeTokenType, UnaryExpression>> operatorWithExpressionList
) implements NonTerminatorType, Precalculable {
    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatorExpressionWithCategoryCodeForEachPairDetailedRepresentation(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(firstExpression, operatorWithExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<MulExp>";
    }

    @Override
    public String toString() {
        return "MultiplicativeExpression{" +
                "firstExpression=" + firstExpression +
                ", operatorWithExpressionList=" + operatorWithExpressionList +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        var product = firstExpression.calculateToInt(symbolManager);
        for (var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            if (operator instanceof MultiplyToken) {
                product *= expression.calculateToInt(symbolManager);
            } else if (operator instanceof DivideToken) {
                product /= expression.calculateToInt(symbolManager);
            } else if (operator instanceof ModulusToken) {
                product %= expression.calculateToInt(symbolManager);
            } else {
                throw new RuntimeException();
            }
        }
        return product;
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        firstExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        for (var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            expression.generatePcode(symbolManager, pcodeList, errorHandler);
            if (operator instanceof MultiplyToken) {
                pcodeList.add(new Operate(Operate.Opcode.MULTIPLY));
            } else if (operator instanceof DivideToken) {
                pcodeList.add(new Operate(Operate.Opcode.DIVIDE));
            } else if (operator instanceof ModulusToken) {
                pcodeList.add(new Operate(Operate.Opcode.MODULUS));
            } else {
                throw new RuntimeException("Unsupport: " + operator.getClass());
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