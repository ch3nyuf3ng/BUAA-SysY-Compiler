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
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class ForStatementSelection implements SelectionType {
    private final ForToken forToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Optional<ForStatement> initStatement;
    private final SemicolonToken semicolonToken1;
    private final Optional<Condition> condition;
    private final SemicolonToken semicolonToken2;
    private final Optional<ForStatement> iterateStatement;
    private final RightParenthesisToken rightParenthesisToken;
    private final Statement statement;

    public ForStatementSelection(
            ForToken forToken,
            LeftParenthesisToken leftParenthesisToken,
            Optional<ForStatement> initStatement,
            SemicolonToken semicolonToken1,
            Optional<Condition> condition,
            SemicolonToken semicolonToken2,
            Optional<ForStatement> iterateStatement,
            RightParenthesisToken rightParenthesisToken,
            Statement statement
    ) {
        this.forToken = Objects.requireNonNull(forToken);
        this.leftParenthesisToken = Objects.requireNonNull(leftParenthesisToken);
        this.initStatement = Objects.requireNonNull(initStatement);
        this.semicolonToken1 = Objects.requireNonNull(semicolonToken1);
        this.condition = Objects.requireNonNull(condition);
        this.semicolonToken2 = Objects.requireNonNull(semicolonToken2);
        this.iterateStatement = Objects.requireNonNull(iterateStatement);
        this.rightParenthesisToken = Objects.requireNonNull(rightParenthesisToken);
        this.statement = Objects.requireNonNull(statement);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(ForToken.class);
    }

    public static Optional<ForStatementSelection> parse(LexerType lexer) {
        Logger.info("Matching <ForStatementSelection>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var forToken = lexer.tryMatchAndConsumeTokenOf(ForToken.class);
            if (forToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var initStatement = ForStatement.parse(lexer);

            final var semicolonToken1 = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken1.isEmpty()) break parse;

            final var condition = Condition.parse(lexer);

            final var semicolonToken2 = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken2.isEmpty()) break parse;

            final var iterateStatement = ForStatement.parse(lexer);

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var statement = Statement.parse(lexer);
            if (statement.isEmpty()) break parse;

            final var result = new ForStatementSelection(
                    forToken.get(), leftParenthesisToken.get(),
                    initStatement, semicolonToken1.get(),
                    condition, semicolonToken2.get(),
                    iterateStatement, rightParenthesisToken.get(),
                    statement.get()
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
}
