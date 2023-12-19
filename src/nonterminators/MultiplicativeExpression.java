package nonterminators;

import error.ErrorHandler;
import error.exceptions.AssignToConstantException;
import error.exceptions.FuncArgNumUnmatchException;
import error.exceptions.FuncArgTypeUnmatchException;
import error.exceptions.IdentifierUndefineException;
import foundation.Pair;
import foundation.ReprBuilder;
import foundation.protocols.EvaluationType;
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
        return ReprBuilder.binaryOpExpWithCatCodeForEachPairDetailedRepr(
                firstExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return ReprBuilder.binaryOpExRepr(firstExpression, operatorWithExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<MulExp>";
    }

    @Override
    public String toString() {
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException {
        var product = 1;
        product *= firstExpression.calculateToInt(symbolManager);
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
                throw new UnsupportedOperationException();
            }
        }
        return product;
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        try {
            firstExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } catch (IdentifierUndefineException | AssignToConstantException |
                 FuncArgTypeUnmatchException | FuncArgNumUnmatchException e) {
            errorHandler.reportError(e);
        }
        for (var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            try {
                expression.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (IdentifierUndefineException | AssignToConstantException |
                     FuncArgTypeUnmatchException | FuncArgNumUnmatchException e) {
                errorHandler.reportError(e);
            }
            if (operator instanceof MultiplyToken) {
                pcodeList.add(new Operate(Operate.Opcode.MULTIPLY));
            } else if (operator instanceof DivideToken) {
                pcodeList.add(new Operate(Operate.Opcode.DIVIDE));
            } else if (operator instanceof ModulusToken) {
                pcodeList.add(new Operate(Operate.Opcode.MODULUS));
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
        return firstExpression.evaluationType(symbolManager);
    }
}