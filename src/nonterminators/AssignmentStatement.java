package nonterminators;

import error.ErrorHandler;
import error.exceptions.AssignToConstantException;
import error.exceptions.IdentifierUndefineException;
import nonterminators.protocols.StatementType;
import pcode.code.StoreValue;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.AssignToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record AssignmentStatement(
        LeftValue leftValue,
        AssignToken assignToken,
        Expression expression,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation()
                + assignToken.detailedRepresentation()
                + expression.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return leftValue.representation() + " "
                + assignToken.representation() + " "
                + expression.representation()
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
        } else {
            return expression.lastTerminator();
        }
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) throws AssignToConstantException, IdentifierUndefineException {
        expression.generatePcode(symbolManager, pcodeList, errorHandler);
        leftValue.generatePcode(symbolManager, pcodeList, true, errorHandler);
        pcodeList.add(new StoreValue(-1, 0));
    }
}