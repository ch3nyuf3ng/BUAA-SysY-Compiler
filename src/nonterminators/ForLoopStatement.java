package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.StatementType;
import pcode.code.*;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.ForToken;
import terminators.LeftParenthesisToken;
import terminators.RightParenthesisToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ForLoopStatement(
        ForToken forToken,
        LeftParenthesisToken leftParenthesisToken,
        Optional<ForStatement> initStatement,
        SemicolonToken semicolonToken1,
        Optional<Condition> condition,
        SemicolonToken semicolonToken2,
        Optional<ForStatement> iterateStatement,
        RightParenthesisToken rightParenthesisToken,
        Statement statement
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return forToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + initStatement.map(ForStatement::detailedRepresentation).orElse("")
                + semicolonToken1.detailedRepresentation()
                + condition.map(Condition::detailedRepresentation).orElse("")
                + semicolonToken2.detailedRepresentation()
                + iterateStatement.map(ForStatement::detailedRepresentation).orElse("")
                + rightParenthesisToken.detailedRepresentation()
                + statement.detailedRepresentation();
    }

    @Override
    public String representation() {
        return forToken.representation() + " "
                + leftParenthesisToken.representation()
                + initStatement.map(ForStatement::representation).orElse("")
                + semicolonToken1.representation() + " "
                + condition.map(Condition::representation).orElse("")
                + semicolonToken2.representation() + " "
                + iterateStatement.map(ForStatement::representation).orElse("")
                + rightParenthesisToken.representation() + " "
                + statement.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return statement.lastTerminator();
    }

    @Override
    public String toString() {
        return "ForLoopStatement{" +
                "forToken=" + forToken +
                ", leftParenthesisToken=" + leftParenthesisToken +
                ", initStatement=" + initStatement +
                ", semicolonToken1=" + semicolonToken1 +
                ", condition=" + condition +
                ", semicolonToken2=" + semicolonToken2 +
                ", iterateStatement=" + iterateStatement +
                ", rightParenthesisToken=" + rightParenthesisToken +
                ", statement=" + statement +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (initStatement.isPresent()) {
            initStatement.get().generatePcode(symbolManager, pcodeList, errorHandler);
        }
        final var label = "#loop" + "[" + symbolManager.createLoopIndex() + "]";
        pcodeList.add(new Label(label + "_start"));
        if (condition.isPresent()) {
            condition.get().generatePcode(symbolManager, pcodeList, errorHandler);
            pcodeList.add(new JumpIfZero(label + "_end"));
        }
        statement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        pcodeList.add(new Label(label + "_iter"));
        if (iterateStatement.isPresent()) {
            iterateStatement.get().generatePcode(symbolManager, pcodeList, errorHandler);
        }
        pcodeList.add(new Jump(label + "_start"));
        pcodeList.add(new Label(label + "_end"));
        symbolManager.discardLoopIndex();
    }
}
