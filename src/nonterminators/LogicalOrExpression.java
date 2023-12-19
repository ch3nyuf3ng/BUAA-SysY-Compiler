package nonterminators;

import error.ErrorHandler;
import foundation.Pair;
import foundation.ReprBuilder;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.*;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LogicalOrToken;
import terminators.protocols.TokenType;

import java.util.List;

public record LogicalOrExpression(
        LogicalAndExpression firstLogicalAndExpression,
        List<Pair<LogicalOrToken, LogicalAndExpression>> operatorWithExpressionList
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (operatorWithExpressionList.isEmpty()) return firstLogicalAndExpression.lastTerminator();
        final var lastIndex = operatorWithExpressionList.size() - 1;
        final var lastNonTerminator = operatorWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return ReprBuilder.binaryOpExpWithCatCodeForEachPairDetailedRepr(
                firstLogicalAndExpression, operatorWithExpressionList, categoryCode()
        );
    }

    @Override
    public String representation() {
        return ReprBuilder.binaryOpExRepr(
                firstLogicalAndExpression,
                operatorWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<LOrExp>";
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        if (!operatorWithExpressionList.isEmpty()) {
            final var baseLabel = "#or" + "[" + symbolManager.orCount() + "]";
            final var shortcutLabel = baseLabel + "_true";
            final var startLabel = baseLabel + "_start";
            final var endLabel = baseLabel + "_end";
            symbolManager.increaseOrCount();
            pcodeList.add(new Label(startLabel));
            firstLogicalAndExpression.generatePcode(symbolManager, pcodeList, errorHandler);
            for (final var operatorWithExpression : operatorWithExpressionList) {
                pcodeList.add(new JumpIfNonzero(shortcutLabel));
                pcodeList.add(new LoadImmediate(0));
                final var expression = operatorWithExpression.second();
                expression.generatePcode(symbolManager, pcodeList, errorHandler);
                pcodeList.add(new Operate(Operate.Opcode.LOGICAL_OR));
            }
            pcodeList.add(new Jump(endLabel));
            pcodeList.add(new Label(shortcutLabel));
            pcodeList.add(new LoadImmediate(1));
            pcodeList.add(new Label(endLabel));
        } else {
            firstLogicalAndExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        }
    }
}
