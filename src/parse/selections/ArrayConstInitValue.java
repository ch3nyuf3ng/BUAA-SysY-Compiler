package parse.selections;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.LeftBraceToken;
import lex.token.RightBraceToken;
import parse.nonterminator.ConstInitValue;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArrayConstInitValue implements SelectionType {
    private final LeftBraceToken leftBraceToken;
    private final Optional<ConstInitValue> firstInitValue;
    private final List<Pair<CommaToken, ConstInitValue>> otherInitValueList;
    private final RightBraceToken rightBraceToken;

    private ArrayConstInitValue(
            LeftBraceToken leftBraceToken,
            Optional<ConstInitValue> firstInitValue,
            List<Pair<CommaToken, ConstInitValue>> otherInitValueList,
            RightBraceToken rightBraceToken
    ) {
        this.leftBraceToken = leftBraceToken;
        this.firstInitValue = firstInitValue;
        this.otherInitValueList = otherInitValueList;
        this.rightBraceToken = rightBraceToken;
    }

    public static Optional<ArrayConstInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ArrayConstInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final Optional<ConstInitValue> optionalFirstInitValue = ConstInitValue.parse(lexer);

            final var otherInitValueList = new ArrayList<Pair<CommaToken, ConstInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var anotherInitValue = ConstInitValue.parse(lexer);
                if (anotherInitValue.isEmpty()) break parse;

                otherInitValueList.add(new Pair<>(commaToken.get(), anotherInitValue.get()));
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new ArrayConstInitValue(
                    leftBraceToken.get(), optionalFirstInitValue, otherInitValueList, rightBraceToken.get()
            );
            Logger.info("Matched <ArrayConstInitValue>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ArrayConstInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return rightBraceToken;
    }

    @Override
    public String detailedRepresentation() {
        return firstInitValue.map(initValue -> leftBraceToken.detailedRepresentation()
                + RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                        initValue, otherInitValueList
                  )
                + rightBraceToken.detailedRepresentation()
                ).orElseGet(() ->
                leftBraceToken.detailedRepresentation()
                + rightBraceToken.detailedRepresentation()
        );
    }

    @Override
    public String representation() {
        return firstInitValue.map(initValue -> leftBraceToken.representation()
                + RepresentationBuilder.binaryOperatedConcatenatedRepresentation( initValue, otherInitValueList)
                + rightBraceToken.representation()
                ).orElseGet(() ->
                leftBraceToken.representation()
                + rightBraceToken.representation()
        );
    }

    @Override
    public String categoryCode() {
        return "";
    }
}
