package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.*;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class Statement implements NonTerminatorType, SelectionType {
    private final SelectionType statement;

    public Statement(SelectionType statement) {
        this.statement = Objects.requireNonNull(statement);
    }

    public static boolean isMatchedBeginningTokens(LexerType lexer) {
        return lexer.isMatchedTokenOf(IdentifierToken.class)
                || ExpressionStatement.isMatchedBeginningToken(lexer)
                || Block.isMatchedBeginningToken(lexer)
                || IfStatement.isMatchedBeginningToken(lexer)
                || ForStatementSelection.isMatchedBeginningToken(lexer)
                || BreakStatement.isMatchedBeginningToken(lexer)
                || ContinueStatement.isMatchedBeginningToken(lexer)
                || ReturnStatement.isMatchedBeginningToken(lexer)
                || PrintfStatement.isMatchedBeginningToken(lexer);
    }

    public static Optional<Statement> parse(LexerType lexer) {
        Logger.info("Matching <Statement>.");
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(IdentifierToken.class)) {
            final var expressionStatement = ExpressionStatement.parse(lexer);
            if (expressionStatement.isPresent()) {
                final var result = new Statement(expressionStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }

            final var getIntStatement = GetIntStatement.parse(lexer);
            if (getIntStatement.isPresent()) {
                final var result = new Statement(getIntStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }

            final var assignmentStatement = AssignmentStatement.parse(lexer);
            if (assignmentStatement.isPresent()) {
                final var result = new Statement(assignmentStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (ExpressionStatement.isMatchedBeginningToken(lexer)) {
            final var expressionStatement = ExpressionStatement.parse(lexer);
            if (expressionStatement.isPresent()) {
                final var result = new Statement(expressionStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }
        /**/
        if (Block.isMatchedBeginningToken(lexer)) {
            final var block = Block.parse(lexer);
            if (block.isPresent()) {
                final var result = new Statement(block.get());
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        if (IfStatement.isMatchedBeginningToken(lexer)) {
            final var ifStatement = IfStatement.parse(lexer);
            if (ifStatement.isPresent()) {
                final var result = new Statement(ifStatement.get());
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        if (ForStatementSelection.isMatchedBeginningToken(lexer)) {
            final var forStatementSelection = ForStatementSelection.parse(lexer);
            if (forStatementSelection.isPresent()) {
                final var result = new Statement(forStatementSelection.get());
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        if (BreakStatement.isMatchedBeginningToken(lexer)) {
            final var breakStatement = BreakStatement.parse(lexer);
            if (breakStatement.isPresent()) {
                final var result = new Statement(breakStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (ContinueStatement.isMatchedBeginningToken(lexer)) {
            final var optionalContinueStatement = ContinueStatement.parse(lexer);
            if (optionalContinueStatement.isPresent()) {
                final var continueStatement = optionalContinueStatement.get();
                final var result = new Statement(continueStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (ReturnStatement.isMatchedBeginningToken(lexer)) {
            final var returnStatement = ReturnStatement.parse(lexer);
            if (returnStatement.isPresent()) {
                final var result = new Statement(returnStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (PrintfStatement.isMatchedBeginningToken(lexer)) {
            final var printfStatement = PrintfStatement.parse(lexer);
            if (printfStatement.isPresent()) {
                final var result = new Statement(printfStatement.get());
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <Statement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

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
}
