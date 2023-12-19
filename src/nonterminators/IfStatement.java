package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.StatementType;
import pcode.code.Jump;
import pcode.code.JumpIfZero;
import pcode.code.Label;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.ElseToken;
import terminators.IfToken;
import terminators.LeftParenthesisToken;
import terminators.RightParenthesisToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record IfStatement(
        IfToken ifToken,
        LeftParenthesisToken leftParenthesisToken,
        Condition condition,
        Optional<RightParenthesisToken> rightParenthesisToken,
        Statement ifStatement,
        Optional<ElseToken> elseToken,
        Optional<Statement> elseStatement
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return ifToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + condition.detailedRepresentation()
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
                + ifStatement.detailedRepresentation()
                + elseToken.map(ElseToken::detailedRepresentation).orElse("")
                + elseStatement.map(Statement::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return ifToken.representation() + " "
                + leftParenthesisToken.representation()
                + condition.representation()
                + rightParenthesisToken.map(RightParenthesisToken::representation).orElse("") + " "
                + ifStatement.representation() + " "
                + elseToken.map(ElseToken::representation).orElse("") + " "
                + elseStatement.map(Statement::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return elseStatement.isPresent() ? elseStatement.get().lastTerminator() : ifStatement.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        condition.generatePcode(symbolManager, pcodeList, errorHandler);
        final var ifLabel = "#if" + "[" + symbolManager.ifCount() + "]";
        symbolManager.increaseIfCount();
        pcodeList.add(new JumpIfZero(ifLabel + "_else"));
        pcodeList.add(new Label(ifLabel + "_start"));
        ifStatement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        pcodeList.add(new Jump(ifLabel + "_end"));
        pcodeList.add(new Label(ifLabel + "_else"));
        elseStatement.ifPresent(statement ->
                statement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler)
        );
        pcodeList.add(new Label(ifLabel + "_end"));
    }
}
