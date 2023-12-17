package nonterminators;

import error.ErrorChecker;
import error.ErrorHandler;
import error.FatalErrorException;
import error.errors.FuncArgNumUnmatchError;
import error.errors.FuncArgTypeUnmatchError;
import nonterminators.protocols.UnaryExpressionType;
import pcode.code.CallFunction;
import pcode.protocols.PcodeType;
import symbol.FunctionSymbol;
import symbol.SymbolManager;
import terminators.IdentifierToken;
import terminators.LeftParenthesisToken;
import terminators.RightParenthesisToken;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FuncInvocation(
        IdentifierToken identifierToken,
        LeftParenthesisToken leftParenthesisToken,
        Optional<FuncArgList> funcArgList,
        Optional<RightParenthesisToken> rightParenthesisToken
) implements UnaryExpressionType {
    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + funcArgList.map(FuncArgList::detailedRepresentation).orElse("")
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + leftParenthesisToken.representation()
                + funcArgList.map(FuncArgList::representation).orElse("")
                + rightParenthesisToken.map(RightParenthesisToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (rightParenthesisToken.isPresent()) {
            return rightParenthesisToken.get();
        } else if (funcArgList.isPresent()) {
            return funcArgList.get().lastTerminator();
        } else {
            return leftParenthesisToken;
        }
    }

    @Override
    public String toString() {
        return "FuncInvocation{" +
                "identifierToken=" + identifierToken +
                ", leftParenthesisToken=" + leftParenthesisToken +
                ", funcArgList=" + funcArgList +
                ", rightParenthesisToken=" + rightParenthesisToken +
                '}';
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        ErrorChecker.checkUndefiniedIdentifier(symbolManager, errorHandler, identifierToken);
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty()) {
            throw new RuntimeException();
        }
        if (!(possibleSymbol.get() instanceof FunctionSymbol functionSymbol)) {
            throw new RuntimeException();
        }
        final int argumentsCount = funcArgList.map(FuncArgList::argCount).orElse(0);
        if (argumentsCount != functionSymbol.metadata().parameters().size()) {
            final var error = new FuncArgNumUnmatchError(identifierToken.endingPosition().lineNumber());
            errorHandler.reportFatalError(error);
        }
        final var arguments = new ArrayList<Expression>();
        if (funcArgList.isPresent()) {
            arguments.add(funcArgList.get().firstExpression());
            for (final var commaWithExpression : funcArgList.get().commaWithExpressionList()) {
                arguments.add(commaWithExpression.second());
            }
        }
        final var parameters = functionSymbol.metadata().parameters();
        for (var i = 0; i < arguments.size(); i += 1) {
            final var argument = arguments.get(i);
            final var parameter = parameters.get(i);
            final var error = new FuncArgTypeUnmatchError(identifierToken.endingPosition().lineNumber());
            if (parameter.metadata().isArrayPointer()) {
                if (!argument.isArrayPointer(symbolManager)) {
                    errorHandler.reportFatalError(error);
                }
                final var argumentArrayPointer = argument.arrayPointerType(symbolManager);
                if (!argumentArrayPointer.basicType().hasSameTypeWith(parameter.metadata().basicType())) {
                    errorHandler.reportFatalError(error);
                }
                if (argumentArrayPointer.level() != parameter.metadata().dimensionSizes().size()) {
                    errorHandler.reportFatalError(error);
                }
                argument.generatePcode(symbolManager, pcodeList, errorHandler);
            } else {
                argument.generatePcode(symbolManager, pcodeList, errorHandler);
            }
        }
        pcodeList.add(new CallFunction("#" + identifierToken.identifier() + "_start", argumentsCount));
    }
}