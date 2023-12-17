package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.ArrayPointer;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.PrimaryExpressionType;
import nonterminators.protocols.UnaryExpressionType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record PrimaryExpression(
        PrimaryExpressionType primaryExpression
) implements UnaryExpressionType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return primaryExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return primaryExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<PrimaryExp>";
    }

    @Override
    public TokenType lastTerminator() {
        return primaryExpression.lastTerminator();
    }

    @Override
    public String toString() {
        return "PrimaryExpression{" +
                "primaryExpression=" + primaryExpression +
                '}';
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        if (primaryExpression instanceof LeftValue leftValue) {
            return leftValue.calculateToInt(symbolManager);
        } else if (primaryExpression instanceof ParenthesisedPrimeExpression parenthesisedPrimeExpression) {
            return parenthesisedPrimeExpression.calculateToInt(symbolManager);
        } else if (primaryExpression instanceof SysYNumber number) {
            return number.calculateToInt(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (primaryExpression instanceof ParenthesisedPrimeExpression parenthesisedPrimeExpression) {
            parenthesisedPrimeExpression.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (primaryExpression instanceof SysYNumber sysYNumber) {
            sysYNumber.generatePcode(pcodeList);
        } else if (primaryExpression instanceof LeftValue leftValue) {
            leftValue.generatePcode(symbolManager, pcodeList, false, errorHandler);
        } else {
            throw new RuntimeException();
        }
    }

    public boolean isArrayPointer(SymbolManager symbolManager) {
        if (primaryExpression instanceof ParenthesisedPrimeExpression parenthesisedPrimeExpression) {
            return parenthesisedPrimeExpression.isArrayPointer(symbolManager);
        } else if (primaryExpression instanceof SysYNumber) {
            return false;
        } else if (primaryExpression instanceof LeftValue leftValue) {
            return leftValue.isCalculatedResultArrayPointer(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }

    public ArrayPointer arrayPointerType(SymbolManager symbolManager) {
        if (primaryExpression instanceof ParenthesisedPrimeExpression parenthesisedPrimeExpression) {
            return parenthesisedPrimeExpression.arrayPointerType(symbolManager);
        } else if (primaryExpression instanceof LeftValue leftValue) {
            return leftValue.arrayPointerType(symbolManager);
        } else {
            throw new RuntimeException();
        }
    }
}
