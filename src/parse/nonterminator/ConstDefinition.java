package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.BracketWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstDefinition implements NonTerminatorType {
    private final IdentifierToken identifierToken;
    private final List<BracketWith<ConstExpression>> bracketWithConstExpressionList;
    private final AssignToken assignToken;
    private final ConstInitValue initValue;

    private ConstDefinition(
            IdentifierToken identifierToken,
            List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
            AssignToken assignToken,
            ConstInitValue initValue
    ) {
        this.identifierToken = identifierToken;
        this.bracketWithConstExpressionList = bracketWithConstExpressionList;
        this.assignToken = assignToken;
        this.initValue = initValue;
    }

    public static Optional<ConstDefinition> parse(LexerType lexer) {
        Logger.info("Matching <ConstDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalIdentifierToken = lexer.currentToken()
                    .filter(t -> t instanceof IdentifierToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IdentifierToken) t;
                    });
            if (optionalIdentifierToken.isEmpty()) break parse;
            final var identifierToken = optionalIdentifierToken.get();


            final List<BracketWith<ConstExpression>> bracketWithConstExpressionList = new ArrayList<>();
            while (lexer.currentToken().isPresent()) {
                final var optionalLeftBracketToken = lexer.currentToken()
                        .filter(t -> t instanceof LeftBracketToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (LeftBracketToken) t;
                        });
                if (optionalLeftBracketToken.isEmpty()) break;
                final var leftBracketToken = optionalLeftBracketToken.get();

                final var optionalConstExpression = ConstExpression.parse(lexer);
                if (optionalConstExpression.isEmpty()) break parse;
                final var constExpression = optionalConstExpression.get();

                final var optionalRightBracketToken = lexer.currentToken()
                        .filter(t -> t instanceof RightBracketToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (RightBracketToken) t;
                        });

                bracketWithConstExpressionList.add(new BracketWith<>(
                        leftBracketToken,
                        constExpression,
                        optionalRightBracketToken
                ));
            }

            final var optionalAssignToken = lexer.currentToken()
                    .filter(t -> t instanceof AssignToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (AssignToken) t;
                    });
            if (optionalAssignToken.isEmpty()) break parse;
            final var assignToken = optionalAssignToken.get();

            final var optionalConstInitValue = ConstInitValue.parse(lexer);
            if (optionalConstInitValue.isEmpty()) break parse;
            final var constInitValue = optionalConstInitValue.get();

            final var result = new ConstDefinition(
                    identifierToken,
                    bracketWithConstExpressionList,
                    assignToken,
                    constInitValue
            );
            Logger.info("Matched <ConstDefinition>." + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ConstDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.detailedRepresentation());
        for (final var item : bracketWithConstExpressionList) {
            stringBuilder
                    .append(item.leftBracketToken().detailedRepresentation())
                    .append(item.entity().detailedRepresentation());
            item.optionalRightBracketToken().ifPresent(e -> stringBuilder.append(e.detailedRepresentation()));
        }
        stringBuilder
                .append(assignToken.detailedRepresentation())
                .append(initValue.detailedRepresentation())
                .append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.representation());
        for (final var item : bracketWithConstExpressionList) {
            stringBuilder
                    .append(item.leftBracketToken().representation())
                    .append(item.entity().representation());
            item.optionalRightBracketToken().ifPresent(e -> stringBuilder
                    .append(e.representation())
            );
        }
        stringBuilder
                .append(' ').append(assignToken.representation())
                .append(' ').append(initValue.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<ConstDef>";
    }

    @Override
    public TokenType lastTerminator() {
        return initValue.lastTerminator();
    }
}
