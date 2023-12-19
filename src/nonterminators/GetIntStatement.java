package nonterminators;

import error.ErrorHandler;
import error.exceptions.AssignToConstantException;
import error.exceptions.IdentifierUndefineException;
import nonterminators.protocols.StatementType;
import pcode.code.ReadNumber;
import pcode.code.StoreValue;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.*;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record GetIntStatement(
        LeftValue leftValue,
        AssignToken assignToken,
        GetIntToken getIntToken,
        LeftParenthesisToken leftParenthesisToken,
        Optional<RightParenthesisToken> rightParenthesisToken,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {

    @Override
    public String detailedRepresentation() {
        return leftValue.detailedRepresentation()
                + assignToken.detailedRepresentation()
                + getIntToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return leftValue.representation() + " "
                + assignToken.representation() + " "
                + getIntToken.representation()
                + leftParenthesisToken.representation()
                + rightParenthesisToken.map(RightParenthesisToken::representation).orElse("")
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
        } else if (rightParenthesisToken.isPresent()) {
            return rightParenthesisToken.get();
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
    ) throws AssignToConstantException, IdentifierUndefineException {
        pcodeList.add(new ReadNumber());
        leftValue.generatePcode(symbolManager, pcodeList, true, errorHandler);
        pcodeList.add(new StoreValue(-1, 0));
    }
}
