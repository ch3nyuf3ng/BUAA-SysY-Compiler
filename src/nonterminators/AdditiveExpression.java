package nonterminators;

import error.ErrorHandler;
import error.exceptions.IdentifierUndefineException;
import foundation.Pair;
import foundation.ReprBuilder;
import foundation.protocols.EvaluationType;
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
        return ReprBuilder.binaryOpExpWithCatCodeForEachPairDetailedRepr(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return ReprBuilder.binaryOpExRepr(
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
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException {
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

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
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
                throw new UnsupportedOperationException();
            }
        }
    }

//    public boolean isArrayPointer(SymbolManager symbolManager) throws IdentifierUndefineException {
//        if (operatorWithExpressionList.isEmpty()) {
//            return firstExpression.isArrayPointer(symbolManager);
//        } else {
//            return false;
//        }
//    }
//
//    public ArrayPointerType arrayPointerType(SymbolManager symbolManager) throws IdentifierUndefineException {
//        return firstExpression.arrayPointerType(symbolManager);
//    }

    public EvaluationType evaluationType(SymbolManager symbolManager) throws IdentifierUndefineException {
        return firstExpression().evaluationType(symbolManager);
    }
}
