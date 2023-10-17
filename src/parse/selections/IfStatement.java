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
import tests.foundations.Logger;

import java.util.Optional;

public class IfStatement implements SelectionType {
    private final IfToken ifToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Condition condition;
    private final RightParenthesisToken rightParenthesisToken;
    private final Statement ifStatement;
    private final Optional<ElseToken> optionalElseToken;
    private final Optional<Statement> optionalElseStatement;

    private IfStatement(
            IfToken ifToken,
            LeftParenthesisToken leftParenthesisToken,
            Condition condition,
            RightParenthesisToken rightParenthesisToken,
            Statement ifStatement,
            Optional<ElseToken> optionalElseToken,
            Optional<Statement> optionalElseStatement
    ) {
        this.ifToken = ifToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.condition = condition;
        this.rightParenthesisToken = rightParenthesisToken;
        this.ifStatement = ifStatement;
        this.optionalElseToken = optionalElseToken;
        this.optionalElseStatement = optionalElseStatement;
    }

    public static Optional<IfStatement> parse(LexerType lexer) {
        Logger.info("Matching <IfStatement>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalIfToken = lexer.currentToken()
                    .filter(t -> t instanceof IfToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IfToken) t;
                    });
            if (optionalIfToken.isEmpty()) break parse;
            final var ifToken = optionalIfToken.get();

            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftParenthesisToken) t;
                    });
            if (optionalLeftParenthesisToken.isEmpty()) break parse;
            final var leftParenthesisToken = optionalLeftParenthesisToken.get();

            final var optionalCondition = Condition.parse(lexer);
            if (optionalCondition.isEmpty()) break parse;
            final var condition = optionalCondition.get();

            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightParenthesisToken) t;
                    });
            if (optionalRightParenthesisToken.isEmpty()) break parse;
            final var rightParenthesisToken = optionalRightParenthesisToken.get();

            final var optionalIfStatement = Statement.parse(lexer);
            if (optionalIfStatement.isEmpty()) break parse;
            final var ifStatement = optionalIfStatement.get();

            final Optional<ElseToken> optionalElseToken = lexer.currentToken()
                    .filter(t -> t instanceof ElseToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (ElseToken) t;
                    });

            if (optionalElseToken.isEmpty()) {
                final var result1 = new IfStatement(
                        ifToken,
                        leftParenthesisToken,
                        condition,
                        rightParenthesisToken,
                        ifStatement,
                        Optional.empty(),
                        Optional.empty()
                );
                Logger.info("Matched <IfStatement>:\n" + result1.representation());
                return Optional.of(result1);
            }

            final Optional<Statement> optionalElseStatement = Statement.parse(lexer);
            if (optionalElseStatement.isEmpty()) break parse;

            final var result = new IfStatement(
                    ifToken,
                    leftParenthesisToken,
                    condition,
                    rightParenthesisToken,
                    ifStatement,
                    optionalElseToken,
                    optionalElseStatement
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
                + optionalElseToken.map(ElseToken::detailedRepresentation).orElse("")
                + optionalElseStatement.map(Statement::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return ifToken.representation() + " "
                + leftParenthesisToken.representation()
                + condition.representation()
                + rightParenthesisToken.representation() + " "
                + ifStatement.representation() + " "
                + optionalElseToken.map(ElseToken::representation).orElse("") + " "
                + optionalElseStatement.map(Statement::representation).orElse("");
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
