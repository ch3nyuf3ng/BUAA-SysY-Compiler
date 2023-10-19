package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ElseToken;
import lex.token.IfToken;
import lex.token.LeftParenthesisToken;
import lex.token.RightParenthesisToken;
import parse.nonterminator.Condition;
import parse.nonterminator.Statement;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class IfStatement implements SelectionType {
    private final IfToken ifToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Condition condition;
    private final RightParenthesisToken rightParenthesisToken;
    private final Statement ifStatement;
    private final Optional<ElseToken> elseToken;
    private final Optional<Statement> elseStatement;

    public IfStatement(
            IfToken ifToken,
            LeftParenthesisToken leftParenthesisToken,
            Condition condition,
            RightParenthesisToken rightParenthesisToken,
            Statement ifStatement,
            Optional<ElseToken> elseToken,
            Optional<Statement> elseStatement
    ) {
        this.ifToken = Objects.requireNonNull(ifToken);
        this.leftParenthesisToken = Objects.requireNonNull(leftParenthesisToken);
        this.condition = Objects.requireNonNull(condition);
        this.rightParenthesisToken = Objects.requireNonNull(rightParenthesisToken);
        this.ifStatement = Objects.requireNonNull(ifStatement);
        this.elseToken = Objects.requireNonNull(elseToken);
        this.elseStatement = Objects.requireNonNull(elseStatement);
    }

    public static boolean isMatchedBeginningToken(LexerType lexer) {
        return lexer.isMatchedTokenOf(IfToken.class);
    }

    public static Optional<IfStatement> parse(LexerType lexer) {
        Logger.info("Matching <IfStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var ifToken = lexer.tryMatchAndConsumeTokenOf(IfToken.class);
            if (ifToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var condition = Condition.parse(lexer);
            if (condition.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var ifStatement = Statement.parse(lexer);
            if (ifStatement.isEmpty()) break parse;

            final var elseToken = lexer.tryMatchAndConsumeTokenOf(ElseToken.class);
            if (elseToken.isEmpty()) {
                final var result = new IfStatement(
                        ifToken.get(), leftParenthesisToken.get(),
                        condition.get(), rightParenthesisToken.get(),
                        ifStatement.get(),
                        Optional.empty(), Optional.empty()
                );
                Logger.info("Matched <IfStatement>:\n" + result.representation());
                return Optional.of(result);
            }

            final var elseStatement = Statement.parse(lexer);
            if (elseStatement.isEmpty()) break parse;

            final var result = new IfStatement(
                    ifToken.get(), leftParenthesisToken.get(),
                    condition.get(), rightParenthesisToken.get(),
                    ifStatement.get(), elseToken, elseStatement
            );
            Logger.info("Matched <IfStatement>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <IfStatement>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return ifToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + condition.detailedRepresentation()
                + rightParenthesisToken.detailedRepresentation()
                + ifStatement.detailedRepresentation()
                + elseToken.map(ElseToken::detailedRepresentation).orElse("")
                + elseStatement.map(Statement::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return ifToken.representation() + " "
                + leftParenthesisToken.representation()
                + condition.representation()
                + rightParenthesisToken.representation() + " "
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
        return null;
    }
}
