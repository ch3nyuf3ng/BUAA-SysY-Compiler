package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.ArrayPointer;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.PrimaryExpressionType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LeftParenthesisToken;
import terminators.RightParenthesisToken;
import terminators.protocols.TokenType;

import java.util.List;

public record ParenthesisedPrimeExpression(
        LeftParenthesisToken leftParenthesisToken,
        Expression expression,
        RightParenthesisToken rightParenthesisToken
) implements PrimaryExpressionType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return leftParenthesisToken.detailedRepresentation()
                + expression.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation();
    }

    @Override
    public String representation() {
        return leftParenthesisToken.representation()
                + expression.representation()
                + rightParenthesisToken.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return rightParenthesisToken;
    }

    @Override
    public String toString() {
        return "ParenthesisedPrimeExpression{" +
                "leftParenthesisToken=" + leftParenthesisToken +
                ", expression=" + expression +
                ", rightParenthesisToken=" + rightParenthesisToken +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        return expression.calculateToInt(symbolManager);
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        expression.generatePcode(symbolManager, pcodeList, errorHandler);
    }

    public boolean isArrayPointer(SymbolManager symbolManager) {
        return expression.isArrayPointer(symbolManager);
    }

    public ArrayPointer arrayPointerType(SymbolManager symbolManager) {
        return expression.arrayPointerType(symbolManager);
    }
}
