package nonterminators;

import error.ErrorHandler;
import foundation.Pair;
import foundation.ReprBuilder;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.Operate;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.EqualToken;
import terminators.NotEqualToken;
import terminators.protocols.EqualityTokenType;
import terminators.protocols.TokenType;

import java.util.List;

public record EqualityExpression(
        RelationalExpression firstExpression,
        List<Pair<EqualityTokenType, RelationalExpression>> operatorWithExpressionList
) implements NonTerminatorType {
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
        return ReprBuilder.binaryOpExRepr(
                firstExpression, operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<EqExp>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        firstExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        for (final var operatorWithExpression : operatorWithExpressionList) {
            final var operator = operatorWithExpression.first();
            final var expression = operatorWithExpression.second();
            expression.generatePcode(symbolManager, pcodeList, errorHandler);
            if (operator instanceof EqualToken) {
                pcodeList.add(new Operate(Operate.Opcode.EQUAL));
            } else if (operator instanceof NotEqualToken) {
                pcodeList.add(new Operate(Operate.Opcode.NOT_EQUAL));
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
