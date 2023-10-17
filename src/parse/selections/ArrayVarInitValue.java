package parse.selections;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.LeftBraceToken;
import lex.token.RightBraceToken;
import parse.nonterminator.VarInitValue;
import parse.protocol.SelectionType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArrayVarInitValue implements SelectionType {
    private final LeftBraceToken leftBraceToken;
    private final Optional<VarInitValue> optionalFirstInitValue;
    private final List<CommaWith<VarInitValue>> otherInitValueList;
    private final RightBraceToken rightBraceToken;

    public ArrayVarInitValue(
            LeftBraceToken leftBraceToken,
            Optional<VarInitValue> optionalFirstInitValue,
            List<CommaWith<VarInitValue>> otherInitValueList,
            RightBraceToken rightBraceToken
    ) {
        this.leftBraceToken = leftBraceToken;
        this.optionalFirstInitValue = optionalFirstInitValue;
        this.otherInitValueList = otherInitValueList;
        this.rightBraceToken = rightBraceToken;
    }

    public static Optional<ArrayVarInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ArrayVarInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalLeftBraceToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftBraceToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftBraceToken) t;
                    });
            if (optionalLeftBraceToken.isEmpty()) break parse;
            final var leftBraceToken = optionalLeftBraceToken.get();

            final Optional<VarInitValue> optionalFirstInitValue = VarInitValue.parse(lexer);

            final var otherInitValueList = new ArrayList<CommaWith<VarInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalAnotherInitValue = VarInitValue.parse(lexer);
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

            final var result = new ArrayVarInitValue(
                    leftBraceToken,
                    optionalFirstInitValue,
                    otherInitValueList,
                    rightBraceToken
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
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(leftBraceToken.detailedRepresentation());
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
        stringBuilder.append(leftBraceToken.representation());
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

    @Override
    public TokenType lastTerminator() {
        return rightBraceToken;
    }
}