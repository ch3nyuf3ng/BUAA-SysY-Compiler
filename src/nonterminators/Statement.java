package nonterminators;

import error.ErrorHandler;
import error.exceptions.*;
import nonterminators.protocols.BlockItemType;
import nonterminators.protocols.StatementType;
import pcode.code.BlockEnd;
import pcode.code.BlockStart;
import pcode.code.DebugPcode;
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
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        if (DebugPcode.Enable) {
            pcodeList.add(new DebugPcode("Statement: " + statement.representation()));
        }
        if (statement instanceof AssignmentStatement assignmentStatement) {
            try {
                assignmentStatement.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (AssignToConstantException | IdentifierUndefineException e) {
                errorHandler.reportError(e);
            }
        } else if (statement instanceof Block block) {
            symbolManager.createSymbolTable();
            pcodeList.add(new BlockStart());
            block.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
            symbolManager.tracebackSymbolTable();
            pcodeList.add(new BlockEnd(false));
        } else if (statement instanceof BreakStatement breakStatement) {
            try {
                breakStatement.generatePcode(symbolManager, pcodeList);
            } catch (BreakOrContinueMisusedException e) {
                errorHandler.reportError(e);
            }
        } else if (statement instanceof ContinueStatement continueStatement) {
            try {
                continueStatement.generatePcode(symbolManager, pcodeList);
            } catch (BreakOrContinueMisusedException e) {
                errorHandler.reportError(e);
            }
        } else if (statement instanceof ExpressionStatement expressionStatement) {
            expressionStatement.generatePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof ForLoopStatement forLoopStatement) {
            forLoopStatement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof GetIntStatement getIntStatement) {
            try {
                getIntStatement.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (AssignToConstantException | IdentifierUndefineException e) {
                errorHandler.reportError(e);
            }
        } else if (statement instanceof IfStatement ifStatement) {
            ifStatement.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (statement instanceof PrintfStatement printfStatement) {
            try {
                printfStatement.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (FormatStringArgCountUnmatchException e) {
                errorHandler.reportError(e);
            }
        } else if (statement instanceof ReturnStatement returnStatement) {
            try {
                returnStatement.generatePcode(symbolManager, pcodeList, errorHandler);
            } catch (ReturnValueInVoidFuncException e) {
                errorHandler.reportError(e);
            }
        } else {
            throw new RuntimeException();
        }
    }
}
