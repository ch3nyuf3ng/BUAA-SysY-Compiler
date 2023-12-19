package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.NonTerminatorType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record Condition(LogicalOrExpression logicalOrExpression) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        return logicalOrExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return logicalOrExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<Cond>";
    }

    @Override
    public TokenType lastTerminator() {
        return logicalOrExpression.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        logicalOrExpression.generatePcode(symbolManager, pcodeList, errorHandler);
    }
}
