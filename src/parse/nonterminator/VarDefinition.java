package parse.nonterminator;

import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.AssignToken;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.BracketWith;
import foundation.Logger;

import java.util.*;

public class VarDefinition implements NonTerminatorType {
    private final IdentifierToken identifierToken;
    private final List<BracketWith<ConstExpression>> bracketWithConstExpressionList;
    private final Optional<AssignToken> assignToken;
    private final Optional<VarInitValue> varInitValue;

    public VarDefinition(
            IdentifierToken identifierToken,
            List<BracketWith<ConstExpression>> bracketWithConstExpressionList,
            Optional<AssignToken> assignToken,
            Optional<VarInitValue> varInitValue
    ) {
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.bracketWithConstExpressionList = Collections.unmodifiableList(bracketWithConstExpressionList);
        this.assignToken = Objects.requireNonNull(assignToken);
        this.varInitValue = Objects.requireNonNull(varInitValue);
    }

    public static Optional<VarDefinition> parse(LexerType lexer) {
        Logger.info("Matching <VarDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (lexer.isMatchedTokenOf(LeftBracketToken.class)) {
                final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (leftBracketToken.isEmpty()) break;

                final var constExpression = ConstExpression.parse(lexer);
                if (constExpression.isEmpty()) break parse;

                final var optionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);
                if (optionalRightBracketToken.isEmpty()) Logger.warn("Tolerated a right bracket missing.");

                bracketWithConstExpressionList.add(new BracketWith<>(leftBracketToken.get(),
                        constExpression.get(),
                        optionalRightBracketToken
                ));
            }

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) {
                final var result = new VarDefinition(
                        identifierToken.get(), bracketWithConstExpressionList, Optional.empty(), Optional.empty()
                );
                Logger.info("Matched <VarDefinition>: " + result.representation());
                return Optional.of(result);
            }

            final var varInitValue = VarInitValue.parse(lexer);
            if (varInitValue.isEmpty()) break parse;

            final var result = new VarDefinition(
                    identifierToken.get(), bracketWithConstExpressionList, assignToken, varInitValue
            );
            Logger.info("Matched <VarDefinition>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <VarDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithConstExpressionList)
                + assignToken.map(AssignToken::detailedRepresentation).orElse("")
                + varInitValue.map(VarInitValue::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(identifierToken.representation());
        for (final var i : bracketWithConstExpressionList) {
            stringBuilder.append(i.leftBracketToken().representation()).append(i.entity().representation());
            i.rightBracketToken().ifPresent(x -> stringBuilder.append(x.representation()));
        }
        assignToken.ifPresent(t -> stringBuilder.append(' ').append(t.representation()));
        varInitValue.ifPresent(t -> stringBuilder.append(' ').append(t.representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<VarDef>";
    }

    @Override
    public TokenType lastTerminator() {
        if (varInitValue.isPresent()) {
            return varInitValue.get().lastTerminator();
        }
        if (!bracketWithConstExpressionList.isEmpty()) {
            final var lastIndex = bracketWithConstExpressionList.size() - 1;
            final var lastItem = bracketWithConstExpressionList.get(lastIndex);
            if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
            return lastItem.entity().lastTerminator();
        }
        return identifierToken;
    }
}
