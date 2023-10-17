package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class FuncDefinition implements NonTerminatorType {
    private final FuncType funcType;
    private final IdentifierToken identifierToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Optional<FuncParamList> optionalFuncParaList;
    private final RightParenthesisToken rightParenthesisToken;
    private final Block block;

    private FuncDefinition(
            FuncType funcType,
            IdentifierToken identifierToken,
            LeftParenthesisToken leftParenthesisToken,
            Optional<FuncParamList> optionalFuncParaList,
            RightParenthesisToken rightParenthesisToken,
            Block block
    ) {
        this.funcType = funcType;
        this.identifierToken = identifierToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.optionalFuncParaList = optionalFuncParaList;
        this.rightParenthesisToken = rightParenthesisToken;
        this.block = block;
    }

    public static boolean matchBeginTokens(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.currentToken()
                .filter(t -> t instanceof IntToken || t instanceof VoidToken)
                .flatMap(t -> {
                    lexer.consumeToken();
                    return lexer.currentToken();
                })
                .filter(t ->  t instanceof IdentifierToken)
                .flatMap(t -> {
                    lexer.consumeToken();
                    return lexer.currentToken();
                })
                .filter(t -> t instanceof LeftParenthesisToken).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    public static Optional<FuncDefinition> parse(LexerType lexer) {
        Logger.info("Matching <FuncDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalFuncType = FuncType.parse(lexer);
            if (optionalFuncType.isEmpty()) break parse;
            final var funcType = optionalFuncType.get();

            final var optionalIdentifierToken =  lexer.currentToken()
                    .filter(t -> t instanceof IdentifierToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (IdentifierToken) t;
                    });
            if (optionalIdentifierToken.isEmpty()) break parse;
            final var identifierToken = optionalIdentifierToken.get();

            final var optionalLeftParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (LeftParenthesisToken) t;
                    });
            if (optionalLeftParenthesisToken.isEmpty()) break parse;
            final var leftParenthesisToken = optionalLeftParenthesisToken.get();

            final var optionalFuncParaList = FuncParamList.parse(lexer);

            final var optionalRightParenthesisToken = lexer.currentToken()
                    .filter(t -> t instanceof RightParenthesisToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (RightParenthesisToken) t;
                    });
            if (optionalRightParenthesisToken.isEmpty()) break parse;
            final var rightParenthesisToken = optionalRightParenthesisToken.get();

            final var optionalBlock = Block.parse(lexer);
            if (optionalBlock.isEmpty()) break parse;
            final var block = optionalBlock.get();

            final var result = new FuncDefinition(
                    funcType,
                    identifierToken,
                    leftParenthesisToken,
                    optionalFuncParaList,
                    rightParenthesisToken,
                    block
            );
            Logger.info("Matched <FuncDefinition>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(funcType.detailedRepresentation())
                .append(identifierToken.detailedRepresentation())
                .append(leftParenthesisToken.detailedRepresentation());
        optionalFuncParaList.ifPresent(x -> stringBuilder.append(x.detailedRepresentation()));
        stringBuilder
                .append(rightParenthesisToken.detailedRepresentation())
                .append(block.detailedRepresentation())
                .append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(funcType.representation()).append(' ')
                .append(identifierToken.representation())
                .append(leftParenthesisToken.representation());
        optionalFuncParaList.ifPresent(x -> stringBuilder
                .append(x.representation())
        );
        stringBuilder
                .append(rightParenthesisToken.representation()).append(' ')
                .append(block.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncDef>";
    }
}
