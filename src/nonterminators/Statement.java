package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.BlockItemType;
import nonterminators.protocols.StatementType;
import pcode.code.BlockEnd;
import pcode.code.BlockStart;
import pcode.code.Debug;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record Statement(
        StatementType statement
) implements BlockItemType {
    @Override
    public String detailedRepresentation() {
        return statement.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return statement.representation();
    }

    @Override
    public String categoryCode() {
        return "<Stmt>";
    }

    @Override
    public TokenType lastTerminator() {
        return statement.lastTerminator();
    }

    @Override
    public String toString() {
        return "Statement{" +
                "statement=" + statement +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (Debug.Enable) {
            pcodeList.add(new Debug("Statement: " + statement.representation()));
        }
        if (statement instanceof AssignmentStatement assignmentStatement) {
            assignmentStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof Block block) {
            symbolManager.createSymbolTable();
            pcodeList.add(new BlockStart());
            block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
            symbolManager.tracebackSymbolTable();
            pcodeList.add(new BlockEnd(false));
        } else if (statement instanceof BreakStatement breakStatement) {
            breakStatement.generatePcode(symbolManager, pcodeList);
        } else if (statement instanceof ContinueStatement continueStatement) {
            continueStatement.generatePcode(symbolManager, pcodeList);
        } else if (statement instanceof ExpressionStatement expressionStatement) {
            expressionStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof ForLoopStatement forLoopStatement) {
            forLoopStatement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof GetIntStatement getIntStatement) {
            getIntStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof IfStatement ifStatement) {
            ifStatement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof PrintfStatement printfStatement) {
            printfStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof ReturnStatement returnStatement) {
            returnStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else {
            throw new RuntimeException();
        }
    }
}
