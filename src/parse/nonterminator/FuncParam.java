package parse.nonterminator;

import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.BracketWith;
import foundation.Logger;

import java.util.*;

public class FuncParam implements NonTerminatorType {
    private final BasicType basicType;
    private final IdentifierToken identifierToken;
    private final Optional<LeftBracketToken> leftBracketToken;
    private final Optional<RightBracketToken> rightBracketToken;
    private final List<BracketWith<ConstExpression>> bracketWithEntityList;

    public FuncParam(
            BasicType basicType,
            IdentifierToken identifierToken,
            Optional<LeftBracketToken> leftBracketToken,
            Optional<RightBracketToken> rightBracketToken,
            List<BracketWith<ConstExpression>> bracketWithEntityList
    ) {
        this.basicType = Objects.requireNonNull(basicType);
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.leftBracketToken = Objects.requireNonNull(leftBracketToken);
        this.rightBracketToken = Objects.requireNonNull(rightBracketToken);
        this.bracketWithEntityList = Collections.unmodifiableList(bracketWithEntityList);
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
            if (leftBracketToken.isEmpty()) return Optional.of(new FuncParam(
                    basicType.get(), identifierToken.get(), Optional.empty(),
                    Optional.empty(), Collections.emptyList()
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

                bracketWithConstExpressionList.add(new BracketWith<>(
                        additionalLeftBracketToken.get(), constExpression.get(), additionalRightBracketToken
                ));
            }

            final var result = new FuncParam(
                    basicType.get(), identifierToken.get(), leftBracketToken,
                    rightBracketToken, bracketWithConstExpressionList
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
            if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
            return lastItem.entity().lastTerminator();
        }
        if (rightBracketToken.isPresent()) return rightBracketToken.get();
        return identifierToken;
    }

    @Override
    public String detailedRepresentation() {
        return basicType.detailedRepresentation()
                + identifierToken.detailedRepresentation()
                + leftBracketToken.map(LeftBracketToken::detailedRepresentation).orElse("")
                + rightBracketToken.map(RightBracketToken::detailedRepresentation).orElse("")
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithEntityList)
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + identifierToken.representation()
                + leftBracketToken.map(LeftBracketToken::representation).orElse("")
                + rightBracketToken.map(RightBracketToken::representation).orElse("")
                + RepresentationBuilder.bracketWithNonTerminatorRepresentation(bracketWithEntityList);
    }

    @Override
    public String categoryCode() {
        return "<FuncFParam>";
    }
}
