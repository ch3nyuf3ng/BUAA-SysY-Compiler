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
import java.util.Objects;
import java.util.Optional;

public class VarDefinition implements NonTerminatorType {
    private final IdentifierToken identifierToken;
    private final List<BracketWith<ConstExpression>> bracketWithConstExpressionList;
    private final Optional<AssignToken> optionalAssignToken;
    private final Optional<VarInitValue> optionalVarInitValue;

    private VarDefinition(
            IdentifierToken identifierToken,
            List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
            Optional<AssignToken> optionalAssignToken,
            Optional<VarInitValue> optionalVarInitValue
    ) {
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.bracketWithConstExpressionList = Objects.requireNonNull(bracketWithConstExpressionList);
        this.optionalAssignToken = Objects.requireNonNull(optionalAssignToken);
        this.optionalVarInitValue = Objects.requireNonNull(optionalVarInitValue);
    }

    public static Optional<VarDefinition> parse(LexerType lexer) {
        Logger.info("Matching <VarDefinition>.");
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

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (true) {
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
            final var result1 = new VarDefinition(
                    identifierToken,
                    bracketWithConstExpressionList,
                    Optional.empty(),
                    Optional.empty()
            );
            if (optionalAssignToken.isEmpty()) {
                Logger.info("Matched <VarDefinition>: " + result1.representation());
                return Optional.of(result1);
            }

            final var optionalVarInitValue = VarInitValue.parse(lexer);

            final var result2 = new VarDefinition(
                    identifierToken,
                    bracketWithConstExpressionList,
                    optionalAssignToken,
                    optionalVarInitValue
            );
            Logger.info("Matched <VarDefinition>: " + result2.representation());
            return Optional.of(result2);
        }

        Logger.info("Failed to match <VarDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.detailedRepresentation());
        for (final var i : bracketWithConstExpressionList) {
            stringBuilder
                    .append(i.leftBracketToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
            i.optionalRightBracketToken().ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        }
        optionalAssignToken.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        optionalVarInitValue.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.representation());
        for (final var i : bracketWithConstExpressionList) {
            stringBuilder
                    .append(i.leftBracketToken().representation())
                    .append(i.entity().representation());
            i.optionalRightBracketToken().ifPresent(x -> stringBuilder
                    .append(x.representation()));
        }
        optionalAssignToken.ifPresent(t -> stringBuilder
                .append(' ').append(t.representation()));
        optionalVarInitValue.ifPresent(t -> stringBuilder
                .append(' ').append(t.representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<VarDef>";
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalVarInitValue.isPresent()) {
            return optionalVarInitValue.get().lastTerminator();
        }
        if (!bracketWithConstExpressionList.isEmpty()) {
            final var lastIndex = bracketWithConstExpressionList.size() - 1;
            final var lastItem = bracketWithConstExpressionList.get(lastIndex);
            if (lastItem.optionalRightBracketToken().isPresent()) {
                return lastItem.optionalRightBracketToken().get();
            }
            return lastItem.entity().lastTerminator();
        }
        return identifierToken;
    }
}
