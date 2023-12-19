package nonterminators;

import error.ErrorHandler;
import foundation.Pair;
import foundation.ReprBuilder;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.*;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LogicalAndToken;
import terminators.protocols.TokenType;

import java.util.List;

public record LogicalAndExpression(
        EqualityExpression firstEqualityExpression,
        List<Pair<LogicalAndToken, EqualityExpression>> operatorWithExpressionList
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstEqualityExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return ReprBuilder.binaryOpExpWithCatCodeForEachPairDetailedRepr(
                firstEqualityExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return ReprBuilder.binaryOpExRepr(
                firstEqualityExpression,
                operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<LAndExp>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        if (!operatorWithExpressionList.isEmpty()) {
            final var baseLabel = "#and" + "[" + symbolManager.andCount() + "]";
            final var shortcutLabel = baseLabel + "_shortcut";
            final var startLabel = baseLabel + "_start";
            final var endLabel = baseLabel + "_end";
            symbolManager.increaseAndCount();
            pcodeList.add(new Label(startLabel));
            firstEqualityExpression.generatePcode(symbolManager, pcodeList, errorHandler);
            for (final var operatorWithExpression : operatorWithExpressionList) {
                pcodeList.add(new JumpIfZero(shortcutLabel));
                pcodeList.add(new LoadImmediate(1));
                final var expression = operatorWithExpression.second();
                expression.generatePcode(symbolManager, pcodeList, errorHandler);
                pcodeList.add(new Operate(Operate.Opcode.LOGICAL_AND));
            }
            pcodeList.add(new Jump(endLabel));
            pcodeList.add(new Label(shortcutLabel));
            pcodeList.add(new LoadImmediate(0));
            pcodeList.add(new Label(endLabel));
        } else {
            firstEqualityExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        }
    }
}
