package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.StoreValue;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.AssignToken;
import terminators.protocols.TokenType;

import java.util.List;

public record ForStatement(
        LeftValue leftValue,
        AssignToken assignToken,
        Expression expression
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        return expression.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation() + assignToken.detailedRepresentation()
                + expression.detailedRepresentation() + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return leftValue.representation() + ' ' + assignToken.representation() + ' ' + expression.representation();
    }

    @Override
    public String categoryCode() {
        return "<ForStmt>";
    }

    @Override
    public String toString() {
        return "ForStatement{" +
                "leftValue=" + leftValue +
                ", assignToken=" + assignToken +
                ", expression=" + expression +
                '}';
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        expression.generatePcode(symbolManager, pcodeList, errorHandler);
        leftValue.generatePcode(symbolManager, pcodeList, true, errorHandler);
        pcodeList.add(new StoreValue(-1, 0));
    }
}
