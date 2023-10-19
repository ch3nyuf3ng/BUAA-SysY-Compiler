package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.FuncInvocation;
import parse.selections.UnaryOperatedExpression;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class UnaryExpression implements NonTerminatorType {
    private final SelectionType unaryExpression;

    public UnaryExpression(SelectionType unaryExpression) {
        this.unaryExpression = Objects.requireNonNull(unaryExpression);
    }

    public static Optional<UnaryExpression> parse(LexerType lexer) {
        Logger.info("Matching <UnaryExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        if (FuncInvocation.isMatchedBeginningTokens(lexer)) {
            final var funcInvocation = FuncInvocation.parse(lexer);
            if (funcInvocation.isPresent()) {
                final var result = new UnaryExpression(funcInvocation.get());
                Logger.info("Matched <UnaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (PrimaryExpression.isMatchedBeginningToken(lexer)) {
            final var primaryExpression = PrimaryExpression.parse(lexer);
            if (primaryExpression.isPresent()) {
                final var result = new UnaryExpression(primaryExpression.get());
                Logger.info("Matched <UnaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (UnaryOperatedExpression.isMatchedBeginningToken(lexer)) {
            final var unaryOperatedExpression = UnaryOperatedExpression.parse(lexer);
            if (unaryOperatedExpression.isPresent()) {
                final var result = new UnaryExpression(unaryOperatedExpression.get());
                Logger.info("Matched <UnaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <UnaryExpression>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return unaryExpression.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return unaryExpression.representation();
    }

    @Override
    public String categoryCode() {
        return "<UnaryExp>";
    }

    @Override
    public TokenType lastTerminator() {
        return unaryExpression.lastTerminator();
    }
}
