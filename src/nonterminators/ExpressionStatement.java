package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.StatementType;
import pcode.code.StackPointerMove;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ExpressionStatement(
        Optional<Expression> expression,
        SemicolonToken semicolonToken
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return expression.map(value -> value.detailedRepresentation() + semicolonToken.detailedRepresentation())
                .orElseGet(semicolonToken::detailedRepresentation);
    }

    @Override
    public String representation() {
        return expression.map(value -> value.representation() + semicolonToken.representation()).orElseGet(
                semicolonToken::representation);
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return semicolonToken;
    }

    @Override
    public String toString() {
        return "ExpressionStatement{" +
                "expression=" + expression +
                ", semicolonToken=" + semicolonToken +
                '}';
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        expression.ifPresent(value -> {
            try {
                value.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (FatalErrorException e) {
                throw new RuntimeException(e);
            }
            pcodeList.add(new StackPointerMove(-1));
        });
    }
}
