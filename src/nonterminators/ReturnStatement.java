package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.StatementType;
import pcode.code.BlockEnd;
import pcode.code.ReturnFunction;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.ReturnToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ReturnStatement(
        ReturnToken returnToken,
        Optional<Expression> expression,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return returnToken.detailedRepresentation()
                + expression.map(Expression::detailedRepresentation).orElse("")
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return returnToken.representation()
                + expression.map(Expression::representation).orElse("")
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) {
            return semicolonToken.get();
        } else if (expression.isPresent()) {
            return expression.get().lastTerminator();
        } else {
            return returnToken;
        }
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
//        final var functionSymbol = symbolManager.getCurrentDefiningFunction();
//        final var noReturnValue = functionSymbol.metadata().returnType().funcType() instanceof VoidToken;
//        if (noReturnValue && expression.isPresent()) {
//            final var error = new ExcessReturnError(returnToken.endingPosition().lineNumber());
//            errorHandler.reportFatalError(error);
//        }
        expression.ifPresent(value -> {
            try {
                value.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (FatalErrorException e) {
                throw new RuntimeException(e);
            }
        });
        final var recycleActiveRecordCount = symbolManager.currentDepth() - 1;
        for (var i = 0; i < recycleActiveRecordCount; i += 1) {
            pcodeList.add(new BlockEnd(expression.isPresent()));
        }
        pcodeList.add(new ReturnFunction(expression.isPresent()));
    }
}
