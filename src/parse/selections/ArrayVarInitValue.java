package parse.selections;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.LeftBraceToken;
import lex.token.RightBraceToken;
import parse.nonterminator.VarInitValue;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ArrayVarInitValue implements SelectionType {
    private final LeftBraceToken leftBraceToken;
    private final Optional<VarInitValue> firstInitValue;
    private final List<Pair<CommaToken, VarInitValue>> otherInitValueList;
    private final RightBraceToken rightBraceToken;

    public ArrayVarInitValue(
            LeftBraceToken leftBraceToken,
            Optional<VarInitValue> firstInitValue,
            List<Pair<CommaToken, VarInitValue>> otherInitValueList,
            RightBraceToken rightBraceToken
    ) {
        this.leftBraceToken = Objects.requireNonNull(leftBraceToken);
        this.firstInitValue = Objects.requireNonNull(firstInitValue);
        this.otherInitValueList = Objects.requireNonNull(otherInitValueList);
        this.rightBraceToken = Objects.requireNonNull(rightBraceToken);
    }

    public static Optional<ArrayVarInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ArrayVarInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final Optional<VarInitValue> optionalFirstInitValue = VarInitValue.parse(lexer);

            final var otherInitValueList = new ArrayList<Pair<CommaToken, VarInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var anotherInitValue = VarInitValue.parse(lexer);
                if (anotherInitValue.isEmpty()) break parse;

                otherInitValueList.add(new Pair<>(commaToken.get(), anotherInitValue.get()));
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new ArrayVarInitValue(
                    leftBraceToken.get(), optionalFirstInitValue,
                    otherInitValueList, rightBraceToken.get()
            );
            Logger.info("Matched <ArrayVarInitValue>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ArrayVarInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return firstInitValue.map(varInitValue -> leftBraceToken.detailedRepresentation()
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        varInitValue, otherInitValueList) + rightBraceToken.detailedRepresentation()
                ).orElseGet(() ->
                leftBraceToken.detailedRepresentation()
                + rightBraceToken.detailedRepresentation());
    }

    @Override
    public String representation() {
        return firstInitValue.map(varInitValue -> leftBraceToken.representation()
                + RepresentationBuilder.binaryOperatorExpressionRepresentation(
                        varInitValue, otherInitValueList) + rightBraceToken.representation()
                ).orElseGet(() ->
                leftBraceToken.representation()
                + rightBraceToken.representation()
        );
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return rightBraceToken;
    }
}