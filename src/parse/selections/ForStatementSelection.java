package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ForToken;
import lex.token.LeftParenthesisToken;
import lex.token.RightParenthesisToken;
import lex.token.SemicolonToken;
import parse.nonterminator.Condition;
import parse.nonterminator.ForStatement;
import parse.nonterminator.Statement;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class ForStatementSelection implements SelectionType {
    private final ForToken forToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Optional<ForStatement> optionalInitStatement;
    private final SemicolonToken semicolonToken1;
    private final Optional<Condition> optionalCondition;
    private final SemicolonToken semicolonToken2;
    private final Optional<ForStatement> optionalIterateStatement;
    private final RightParenthesisToken rightParenthesisToken;
    private final Statement statement;

    private ForStatementSelection(
            ForToken forToken,
            LeftParenthesisToken leftParenthesisToken,
            Optional<ForStatement> optionalInitStatement,
            SemicolonToken semicolonToken1,
            Optional<Condition> optionalCondition,
            SemicolonToken semicolonToken2,
            Optional<ForStatement> optionalIterateStatement,
            RightParenthesisToken rightParenthesisToken,
            Statement statement
    ) {
        this.forToken = forToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.optionalInitStatement = optionalInitStatement;
        this.semicolonToken1 = semicolonToken1;
        this.optionalCondition = optionalCondition;
        this.semicolonToken2 = semicolonToken2;
        this.optionalIterateStatement = optionalIterateStatement;
        this.rightParenthesisToken = rightParenthesisToken;
        this.statement = statement;
    }

    public static Optional<ForStatementSelection> parse(LexerType lexer) {
        Logger.info("Matching <ForStatementSelection>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalForToken = lexer.currentToken()
                    .filter(t -> t instanceof ForToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (ForToken) t;
                    });
            if (optionalForToken.isEmpty()) break parse;
            final var forToken = optionalForToken.get();

            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftParenthesisToken) t;
                    });
            if (optionalLeftParenthesisToken.isEmpty()) break parse;
            final var leftParenthesisToken = optionalLeftParenthesisToken.get();

            final Optional<ForStatement> optionalInitStatement = ForStatement.parse(lexer);

            final var optionalSemicolonToken1 = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });
            if (optionalSemicolonToken1.isEmpty()) break parse;
            final var semicolonToken1 = optionalSemicolonToken1.get();


            final Optional<Condition> optionalCondition = Condition.parse(lexer);

            final var optionalSemicolonToken2 = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });
            if (optionalSemicolonToken2.isEmpty()) break parse;
            final var semicolonToken2 = optionalSemicolonToken2.get();

            final Optional<ForStatement> optionalIterateStatement = ForStatement.parse(lexer);

            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightParenthesisToken) t;
                    });
            if (optionalRightParenthesisToken.isEmpty()) break parse;
            final var rightParenthesisToken = optionalRightParenthesisToken.get();

            final var optionalStatement = Statement.parse(lexer);
            if (optionalStatement.isEmpty()) break parse;
            final var statement = optionalStatement.get();

            final var result = new ForStatementSelection(
                    forToken,
                    leftParenthesisToken,
                    optionalInitStatement,
                    semicolonToken1,
                    optionalCondition,
                    semicolonToken2,
                    optionalIterateStatement,
                    rightParenthesisToken,
                    statement
            );
            Logger.info("Matched <ForStatementSelection>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ForStatementSelection>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return forToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + optionalInitStatement.map(ForStatement::detailedRepresentation).orElse("")
                + semicolonToken1.detailedRepresentation()
                + optionalCondition.map(Condition::detailedRepresentation).orElse("")
                + semicolonToken2.detailedRepresentation()
                + optionalIterateStatement.map(ForStatement::detailedRepresentation).orElse("")
                + rightParenthesisToken.detailedRepresentation()
                + statement.detailedRepresentation();
    }

    @Override
    public String representation() {
        return forToken.representation() + " "
                + leftParenthesisToken.representation()
                + optionalInitStatement.map(ForStatement::representation).orElse("")
                + semicolonToken1.representation() + " "
                + optionalCondition.map(Condition::representation).orElse("")
                + semicolonToken2.representation() + " "
                + optionalIterateStatement.map(ForStatement::representation).orElse("")
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
}
