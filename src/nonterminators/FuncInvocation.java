package nonterminators;

import error.ErrorHandler;
import error.exceptions.FuncArgNumUnmatchException;
import error.exceptions.FuncArgTypeUnmatchException;
import error.exceptions.IdentifierUndefineException;
import foundation.Helpers;
import foundation.protocols.EvaluationType;
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
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) throws IdentifierUndefineException, FuncArgTypeUnmatchException, FuncArgNumUnmatchException {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty() || !(possibleSymbol.get() instanceof FunctionSymbol functionSymbol)) {
            throw new IdentifierUndefineException(Helpers.lineNumberOf(identifierToken));
        }
        final int argumentsCount = funcArgList.map(FuncArgList::argCount).orElse(0);
        if (argumentsCount != functionSymbol.metadata().parameters().size()) {
            throw new FuncArgNumUnmatchException(Helpers.lineNumberOf(identifierToken));
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
            if (parameter.metadata().evaluationType().equals(argument.evaluationType(symbolManager))) {
                argument.generatePcode(symbolManager, pcodeList, errorHandler);
            } else {
                throw new FuncArgTypeUnmatchException(Helpers.lineNumberOf(identifierToken));
            }
        }
        pcodeList.add(new CallFunction("#" + identifierToken.identifier() + "_start", argumentsCount));
    }

    public EvaluationType evaluationType(SymbolManager symbolManager) throws IdentifierUndefineException {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty() || !(possibleSymbol.get() instanceof FunctionSymbol functionSymbol)) {
            throw new IdentifierUndefineException(Helpers.lineNumberOf(identifierToken));
        }
        return functionSymbol.metadata().returnType();
    }
}