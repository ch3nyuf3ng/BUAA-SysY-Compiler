package parse.nonterminator;

import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.substructures.BracketWith;
import foundation.Logger;

import java.util.*;

public class LeftValue implements NonTerminatorType, SelectionType {
    private final IdentifierToken identifierToken;
    private final List<BracketWith<Expression>> bracketWithExpressionList;

    public LeftValue(IdentifierToken identifierToken, List<BracketWith<Expression>> bracketWithExpressionList) {
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.bracketWithExpressionList = Collections.unmodifiableList(bracketWithExpressionList);
    }

    public static Optional<LeftValue> parse(LexerType lexer) {
        Logger.info("Matching <LeftValue>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var bracketWithExpressionList = new ArrayList<BracketWith<Expression>>();
            while (true) {
                final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (leftBracketToken.isEmpty()) break;

                final var expression = Expression.parse(lexer);
                if (expression.isEmpty()) break parse;

                final var optionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);

                bracketWithExpressionList.add(new BracketWith<>(leftBracketToken.get(),
                        expression.get(),
                        optionalRightBracketToken
                ));
            }

            final var result = new LeftValue(identifierToken.get(), bracketWithExpressionList);
            Logger.info("Matched <LeftValue>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <LeftValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (bracketWithExpressionList.isEmpty()) return identifierToken;
        final var lastIndex = bracketWithExpressionList.size() - 1;
        final var lastItem = bracketWithExpressionList.get(lastIndex);
        if (lastItem.rightBracketToken().isPresent()) return lastItem.rightBracketToken().get();
        return lastItem.entity().lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return identifierToken.detailedRepresentation()
                + RepresentationBuilder.bracketWithNonTerminatorDetailedRepresentation(bracketWithExpressionList)
                + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return identifierToken.representation()
                + RepresentationBuilder.bracketWithNonTerminatorRepresentation(bracketWithExpressionList);
    }

    @Override
    public String categoryCode() {
        return "<LVal>";
    }
}
