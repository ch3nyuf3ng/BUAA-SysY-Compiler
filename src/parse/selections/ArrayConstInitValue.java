package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.LeftBraceToken;
import lex.token.RightBraceToken;
import parse.nonterminator.ConstInitValue;
import parse.protocol.SelectionType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArrayConstInitValue implements SelectionType {
    private final LeftBraceToken leftBracket;
    private final Optional<ConstInitValue> optionalFirstInitValue;
    private final List<CommaWith<ConstInitValue>> otherInitValueList;
    private final RightBraceToken rightBraceToken;

    private ArrayConstInitValue(
            LeftBraceToken leftBracket,
            Optional<ConstInitValue> optionalFirstInitValue,
            List<CommaWith<ConstInitValue>> otherInitValueList,
            RightBraceToken rightBraceToken
    ) {
        this.leftBracket = leftBracket;
        this.optionalFirstInitValue = optionalFirstInitValue;
        this.otherInitValueList = otherInitValueList;
        this.rightBraceToken = rightBraceToken;
    }

    public static Optional<ArrayConstInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ArrayConstInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalLeftBraceToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftBraceToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftBraceToken) t;
                    });
            if (optionalLeftBraceToken.isEmpty()) break parse;
            final var leftBraceToken = optionalLeftBraceToken.get();

            final Optional<ConstInitValue> optionalFirstInitValue = ConstInitValue.parse(lexer);

            final var otherInitValueList = new ArrayList<CommaWith<ConstInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalAnotherInitValue = ConstInitValue.parse(lexer);
                if (optionalAnotherInitValue.isEmpty()) break parse;
                final var anotherInitValue = optionalAnotherInitValue.get();

                otherInitValueList.add(new CommaWith<>(commaToken, anotherInitValue));
            }

            final var optionalRightBraceToken = lexer.currentToken()
                    .filter(t -> t instanceof RightBraceToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightBraceToken) t;
                    });
            if (optionalRightBraceToken.isEmpty()) break parse;
            final var rightBraceToken = optionalRightBraceToken.get();

            final var result = new ArrayConstInitValue(
                    leftBraceToken,
                    optionalFirstInitValue,
                    otherInitValueList,
                    rightBraceToken
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
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(leftBracket.detailedRepresentation());
        optionalFirstInitValue.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        for (final var i : otherInitValueList) {
            stringBuilder
                    .append(i.commaToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
        }
        stringBuilder.append(rightBraceToken.detailedRepresentation());
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(leftBracket.representation());
        optionalFirstInitValue.ifPresent(t -> stringBuilder.append(t.representation()));
        for (final var i : otherInitValueList) {
            stringBuilder
                    .append(i.commaToken().representation()).append(' ')
                    .append(i.entity().representation());
        }
        stringBuilder.append(rightBraceToken.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "";
    }
}
