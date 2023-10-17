package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.FuncInvocation;
import parse.selections.UnaryOperatedExpression;
import tests.foundations.Logger;

import java.util.Optional;

public class UnaryExpression implements NonTerminatorType {
    private final SelectionType unaryExpression;

    private UnaryExpression(SelectionType unaryExpression) {
        this.unaryExpression = unaryExpression;
    }

    public static Optional<UnaryExpression> parse(LexerType lexer) {
        Logger.info("Matching <UnaryExpression>.");
        final var beginningPosition = lexer.beginningPosition();

        final var optionalFuncInvocationBeginningPosition = lexer.currentToken()
                .filter(t -> t instanceof IdentifierToken)
                .flatMap(t -> {
                    lexer.consumeToken();
                    return lexer.currentToken();
                })
                .filter(t -> t instanceof LeftParenthesisToken);
        lexer.resetPosition(beginningPosition);
        if (optionalFuncInvocationBeginningPosition.isPresent()) {
            final var optionalFuncInvocation = FuncInvocation.parse(lexer);
            if (optionalFuncInvocation.isPresent()) {
                final var funcInvocation = optionalFuncInvocation.get();
                final var result = new UnaryExpression(funcInvocation);
                Logger.info("Matched <UnaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalPrimaryExpressionBeginningToken = lexer.currentToken()
                .filter(t -> t instanceof LeftParenthesisToken
                        || t instanceof IdentifierToken
                        || t instanceof LiteralIntegerToken);
        if (optionalPrimaryExpressionBeginningToken.isPresent()) {
            final var optionalPrimaryExpression = PrimaryExpression.parse(lexer);
            if (optionalPrimaryExpression.isPresent()) {
                final var primaryExpression = optionalPrimaryExpression.get();
                final var result = new UnaryExpression(primaryExpression);
                Logger.info("Matched <UnaryExpression>: " + result.representation());
                return Optional.of(result);
            }
        }

        final var optionalUnaryOperatedExpressionBeginningToken = lexer.currentToken()
                .filter(t -> t instanceof PlusToken || t instanceof MinusToken || t instanceof LogicalNotToken);
        if (optionalUnaryOperatedExpressionBeginningToken.isPresent()) {
            final var optionalUnaryOperatedExpression = UnaryOperatedExpression.parse(lexer);
            if (optionalUnaryOperatedExpression.isPresent()) {
                final var unaryOperatedExpression = optionalUnaryOperatedExpression.get();
                final var result = new UnaryExpression(unaryOperatedExpression);
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
