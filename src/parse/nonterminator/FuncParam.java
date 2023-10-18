package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.BracketWith;
import tests.foundations.Logger;

import java.util.*;

public class FuncParam implements NonTerminatorType {
    private final BasicType basicType;
    private final IdentifierToken identifierToken;
    private final Optional<LeftBracketToken> optionalLeftBracketToken;
    private final Optional<RightBracketToken> optionalRightBracketToken;
    private final List<BracketWith<ConstExpression>> bracketWithEntityList;

    public FuncParam(
            BasicType basicType,
            IdentifierToken identifierToken,
            Optional<LeftBracketToken> optionalLeftBracketToken,
            Optional<RightBracketToken> optionalRightBracketToken,
            List<BracketWith<ConstExpression>> bracketWithEntityList
    ) {
        this.basicType = Objects.requireNonNull(basicType);
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.optionalLeftBracketToken = Objects.requireNonNull(optionalLeftBracketToken);
        this.optionalRightBracketToken = Objects.requireNonNull(optionalRightBracketToken);
        this.bracketWithEntityList = Objects.requireNonNull(bracketWithEntityList);
    }

    public static Optional<FuncParam> parse(LexerType lexer) {
        Logger.info("Matching <FuncParam>.");
        final var beginnningPosition = lexer.beginningPosition();

        parse:
        {
            final var basicType = BasicType.parse(lexer);
            if (basicType.isEmpty()) break parse;

            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
            if (leftBracketToken.isEmpty()) return Optional.of(new FuncParam(basicType.get(),
                    identifierToken.get(),
                    Optional.empty(),
                    Optional.empty(),
                    Collections.emptyList()
            ));

            final var rightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);
            if (rightBracketToken.isEmpty()) break parse;

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (lexer.isMatchedTokenOf(LeftBracketToken.class)) {
                final var additionalLeftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (additionalLeftBracketToken.isEmpty()) break;

                final var constExpression = ConstExpression.parse(lexer);
                if (constExpression.isEmpty()) break;

                final var additionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);

                bracketWithConstExpressionList.add(new BracketWith<>(additionalLeftBracketToken.get(),
                        constExpression.get(),
                        additionalRightBracketToken
                ));
            }

            final var result = new FuncParam(basicType.get(),
                    identifierToken.get(),
                    leftBracketToken,
                    rightBracketToken,
                    bracketWithConstExpressionList
            );
            Logger.info("Matched <FuncParam>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncParam>.");
        lexer.resetPosition(beginnningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (!bracketWithEntityList.isEmpty()) {
            final var lastIndex = bracketWithEntityList.size() - 1;
            final var lastItem = bracketWithEntityList.get(lastIndex);
            if (lastItem.optionalRightBracketToken().isPresent()) return lastItem.optionalRightBracketToken().get();
            return lastItem.entity().lastTerminator();
        }
        if (optionalRightBracketToken.isPresent()) return optionalRightBracketToken.get();
        return identifierToken;
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(basicType.detailedRepresentation()).append(identifierToken.detailedRepresentation());
        optionalLeftBracketToken.ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        optionalRightBracketToken.ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        for (final var i : bracketWithEntityList) {
            stringBuilder.append(i.leftBracketToken().detailedRepresentation()).append(i.entity()
                    .detailedRepresentation());
            i.optionalRightBracketToken().ifPresent(e -> stringBuilder.append(e.detailedRepresentation()));
        }
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(basicType.representation()).append(' ').append(identifierToken.representation());
        optionalLeftBracketToken.ifPresent(x -> stringBuilder.append(x.representation()));
        optionalRightBracketToken.ifPresent(x -> stringBuilder.append(x.representation()));
        for (final var i : bracketWithEntityList) {
            stringBuilder.append(i.leftBracketToken().representation()).append(i.entity().representation());
            i.optionalRightBracketToken().ifPresent(e -> stringBuilder.append(e.representation()));
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncFParam>";
    }
}
