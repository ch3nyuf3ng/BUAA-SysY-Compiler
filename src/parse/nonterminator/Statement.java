package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.*;
import tests.foundations.Logger;

import java.util.Optional;

public class Statement implements NonTerminatorType, SelectionType {
    private final SelectionType statement;

    private Statement(SelectionType statement) {
        this.statement = statement;
    }

    public static Optional<Statement> parse(LexerType lexer) {
        Logger.info("Matching <Statement>.");
        final var beginningPosition = lexer.beginningPosition();

        final var optionalIdentifierToken = lexer.currentToken()
                .filter(t -> t instanceof IdentifierToken);
        if (optionalIdentifierToken.isPresent()) {
            final var optionalExpression = Expression.parse(lexer);
            if (optionalExpression.isPresent()) {
                Logger.info("Matching <ExpressionStatement>.");
                final var optionalSemicolonToken = lexer.currentToken()
                        .filter(t -> t instanceof SemicolonToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (SemicolonToken) t;
                        });
                if (optionalSemicolonToken.isPresent()) {
                    final var result = new Statement(new ExpressionStatement(
                                    optionalExpression, optionalSemicolonToken
                    ));
                    Logger.info("Matched <ExpressionStatement>: " + result.representation());
                    Logger.info("Matched <Statement>: " + result.representation());
                    return Optional.of(result);
                } else {
                    Logger.info("Failed to match <ExpressionStatement>.");
                    lexer.resetPosition(beginningPosition);
                }
            }
            final var optionalGetIntStatement = GetIntStatement.parse(lexer);
            if (optionalGetIntStatement.isPresent()) {
                final var getIntStatement = optionalGetIntStatement.get();
                final var result = new Statement(getIntStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
            final var optionalAssignmentStatement = AssignmentStatement.parse(lexer);
            if (optionalAssignmentStatement.isPresent()) {
                final var assignmentStatement = optionalAssignmentStatement.get();
                final var result = new Statement(assignmentStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalNonSingleLvalExpBeginningToken = lexer.currentToken()
                .filter(t -> t instanceof LeftParenthesisToken
                        || t instanceof LiteralIntegerToken
                        || t instanceof PlusToken
                        || t instanceof MinusToken
                        || t instanceof LogicalNotToken
                        || t instanceof SemicolonToken);
        if (optionalNonSingleLvalExpBeginningToken.isPresent()) {
            final var optionalExpressionStatement = ExpressionStatement.parse(lexer);
            if (optionalExpressionStatement.isPresent()) {
                final var expressionStatement = optionalExpressionStatement.get();
                final var result = new Statement(expressionStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalBlockBeginningToken = lexer.currentToken()
                .filter(t -> t instanceof LeftBraceToken);
        if (optionalBlockBeginningToken.isPresent()) {
            final var optionalBlock = Block.parse(lexer);
            if (optionalBlock.isPresent()) {
                final var block = optionalBlock.get();
                final var result = new Statement(block);
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalIfToken = lexer.currentToken()
                .filter(t -> t instanceof IfToken);
        if (optionalIfToken.isPresent()) {
            final var optionalIfStatement = IfStatement.parse(lexer);
            if (optionalIfStatement.isPresent()) {
                final var ifStatement = optionalIfStatement.get();
                final var result = new Statement(ifStatement);
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalForToken = lexer.currentToken()
                .filter(t -> t instanceof ForToken);
        if (optionalForToken.isPresent()) {
            final var optionalForStatementSelection = ForStatementSelection.parse(lexer);
            if (optionalForStatementSelection.isPresent()) {
                final var forStatementSelection = optionalForStatementSelection.get();
                final var result = new Statement(forStatementSelection);
                Logger.info("Matched <Statement>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalBreakToken = lexer.currentToken()
                .filter(t -> t instanceof BreakToken);
        if (optionalBreakToken.isPresent()) {
            final var optionalBreakStatement = BreakStatement.parse(lexer);
            if (optionalBreakStatement.isPresent()) {
                final var breakStatement = optionalBreakStatement.get();
                final var result = new Statement(breakStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalContinueToken = lexer.currentToken()
                .filter(t -> t instanceof ContinueToken);
        if (optionalContinueToken.isPresent()) {
            final var optionalContinueStatement = ContinueStatement.parse(lexer);
            if (optionalContinueStatement.isPresent()) {
                final var continueStatement = optionalContinueStatement.get();
                final var result = new Statement(continueStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalReturnToken = lexer.currentToken()
                .filter(t -> t instanceof ReturnToken);
        if (optionalReturnToken.isPresent()) {
            final var optionalReturnStatement = ReturnStatement.parse(lexer);
            if (optionalReturnStatement.isPresent()) {
                final var returnStatement = optionalReturnStatement.get();
                final var result = new Statement(returnStatement);
                Logger.info("Matched <Statement>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalPrintfToken = lexer.currentToken()
                .filter(t -> t instanceof PrintfToken);
        if (optionalPrintfToken.isPresent()) {
            final var optionalPrintfStatement = PrintfStatement.parse(lexer);
            if (optionalPrintfStatement.isPresent()) {
                final var printfStatement = optionalPrintfStatement.get();
                final var result = new Statement(printfStatement);
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
        return null;
    }
}
