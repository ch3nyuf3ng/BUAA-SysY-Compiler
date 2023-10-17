package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IdentifierToken;
import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.BracketWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        this.basicType = basicType;
        this.identifierToken = identifierToken;
        this.optionalLeftBracketToken = optionalLeftBracketToken;
        this.optionalRightBracketToken = optionalRightBracketToken;
        this.bracketWithEntityList = bracketWithEntityList;
    }

    public static Optional<FuncParam> parse(LexerType lexer) {
        Logger.info("Matching <FuncParam>.");
        final var beginnningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalBasicType = BasicType.parse(lexer);
            if (optionalBasicType.isEmpty()) break parse;
            final var basicType = optionalBasicType.get();

            final var optionalIdentifierToken = lexer.currentToken()
                    .filter(t -> t instanceof IdentifierToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IdentifierToken) t;
                    });
            if (optionalIdentifierToken.isEmpty()) break parse;
            final var identifierToken = optionalIdentifierToken.get();

            final Optional<LeftBracketToken> optionalLeftBracketToken = lexer.currentToken()
                    .filter(token -> token instanceof LeftBracketToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftBracketToken) t;
                    });
            if (optionalLeftBracketToken.isEmpty()) {
                return Optional.of(new FuncParam(
                        basicType,
                        identifierToken,
                        Optional.empty(),
                        Optional.empty(),
                        Collections.emptyList()
                ));
            }

            final Optional<RightBracketToken> optionalRightBracketToken = lexer.currentToken()
                    .filter(t -> t instanceof RightBracketToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightBracketToken) t;
                    });
            if (optionalRightBracketToken.isEmpty()) break parse;

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (true) {
                final Optional<LeftBracketToken> optionalAdditionalLeftBracketToken = lexer.currentToken()
                        .filter(token -> token instanceof LeftBracketToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (LeftBracketToken) t;
                        });
                if (optionalAdditionalLeftBracketToken.isEmpty()) break;
                final var additionalLeftBracketToken = optionalAdditionalLeftBracketToken.get();

                final var optionalConstExpression = ConstExpression.parse(lexer);
                if (optionalConstExpression.isEmpty()) break;
                final var constExpression = optionalConstExpression.get();

                final var optionalAdditionalRightBracketToken = lexer.currentToken()
                        .filter(t -> t instanceof RightBracketToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (RightBracketToken) t;
                        });

                bracketWithConstExpressionList.add(new BracketWith<>(
                        additionalLeftBracketToken,
                        constExpression,
                        optionalAdditionalRightBracketToken
                ));
            }

            final var result = new FuncParam(
                    basicType,
                    identifierToken,
                    optionalLeftBracketToken,
                    optionalRightBracketToken,
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
            if (lastItem.optionalRightBracketToken().isPresent()) {
                return lastItem.optionalRightBracketToken().get();
            }
            return lastItem.entity().lastTerminator();
        }
        if (optionalRightBracketToken.isPresent()) {
            return optionalRightBracketToken.get();
        }
        return identifierToken;
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(basicType.detailedRepresentation())
                .append(identifierToken.detailedRepresentation());
        optionalLeftBracketToken.ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        optionalRightBracketToken.ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        for (final var i : bracketWithEntityList) {
            stringBuilder
                    .append(i.leftBracketToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
            i.optionalRightBracketToken().ifPresent(e -> stringBuilder.append(e.detailedRepresentation()));
        }
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(basicType.representation()).append(' ')
                .append(identifierToken.representation());
        optionalLeftBracketToken.ifPresent(x -> stringBuilder
                .append(x.representation())
        );
        optionalRightBracketToken.ifPresent(x -> stringBuilder
                .append(x.representation())
        );
        for (final var i : bracketWithEntityList) {
            stringBuilder
                    .append(i.leftBracketToken().representation())
                    .append(i.entity().representation());
            i.optionalRightBracketToken().ifPresent(e -> stringBuilder
                    .append(e.representation())
            );
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncFParam>";
    }
}
