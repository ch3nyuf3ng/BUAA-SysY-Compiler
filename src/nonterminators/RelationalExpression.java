package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.Operate;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.GreaterOrEqualToken;
import terminators.GreaterToken;
import terminators.LessOrEqualToken;
import terminators.LessToken;
import terminators.protocols.RelaitionalOperatorTokenType;
import terminators.protocols.TokenType;

import java.util.List;

public record RelationalExpression(
        AdditiveExpression firstAdditiveExpression,
        List<Pair<RelaitionalOperatorTokenType, AdditiveExpression>> operatorWithExpressionList
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstAdditiveExpression.lastTerminator();
        return operatorWithExpressionList.get(operatorWithExpressionList.size() - 1).second().lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatorExpressionWithCategoryCodeForEachPairDetailedRepresentation(
                firstAdditiveExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(
                firstAdditiveExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<RelExp>";
    }

    @Override
    public String toString() {
        return "RelationalExpression{" +
                "firstAdditiveExpression=" + firstAdditiveExpression +
                ", operatorWithExpressionList=" + operatorWithExpressionList +
                '}';
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        firstAdditiveExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        for (final var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            expression.generatePcode(symbolManager, pcodeList, errorHandler);
            if (operator instanceof LessToken) {
                pcodeList.add(new Operate(Operate.Opcode.LESS));
            } else if (operator instanceof LessOrEqualToken) {
                pcodeList.add(new Operate(Operate.Opcode.LESS_OR_EQUAL));
            } else if (operator instanceof GreaterToken) {
                pcodeList.add(new Operate(Operate.Opcode.GREATER));
            } else if (operator instanceof GreaterOrEqualToken) {
                pcodeList.add(new Operate(Operate.Opcode.GREATER_OR_EQUAL));
            } else {
                throw new RuntimeException();
            }
        }
    }
}
