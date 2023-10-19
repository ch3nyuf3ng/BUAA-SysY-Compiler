package parse.nonterminator;

import lex.protocol.FuncTypeTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class FuncDefinition implements NonTerminatorType {
    private final FuncType funcType;
    private final IdentifierToken identifierToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final Optional<FuncParamList> funcParaList;
    private final RightParenthesisToken rightParenthesisToken;
    private final Block block;

    public FuncDefinition(
            FuncType funcType,
            IdentifierToken identifierToken,
            LeftParenthesisToken leftParenthesisToken,
            Optional<FuncParamList> funcParaList,
            RightParenthesisToken rightParenthesisToken,
            Block block
    ) {
        this.funcType = Objects.requireNonNull(funcType);
        this.identifierToken = Objects.requireNonNull(identifierToken);
        this.leftParenthesisToken = Objects.requireNonNull(leftParenthesisToken);
        this.funcParaList = Objects.requireNonNull(funcParaList);
        this.rightParenthesisToken = Objects.requireNonNull(rightParenthesisToken);
        this.block = Objects.requireNonNull(block);
    }

    public static boolean isMatchedBeginningTokens(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.tryMatchAndConsumeTokenOf(FuncTypeTokenType.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class))
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class)).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    public static Optional<FuncDefinition> parse(LexerType lexer) {
        Logger.info("Matching <FuncDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var funcType = FuncType.parse(lexer);
            if (funcType.isEmpty()) break parse;

            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var funcParaList = FuncParamList.parse(lexer);

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var block = Block.parse(lexer);
            if (block.isEmpty()) break parse;

            final var result = new FuncDefinition(
                    funcType.get(), identifierToken.get(), leftParenthesisToken.get(),
                    funcParaList, rightParenthesisToken.get(), block.get()
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
        return funcType.detailedRepresentation()
                + identifierToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + funcParaList.map(FuncParamList::detailedRepresentation).orElse("")
                + rightParenthesisToken.detailedRepresentation() + block.detailedRepresentation()
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return funcType.representation() + ' '
                + identifierToken.representation()
                + leftParenthesisToken.representation()
                + funcParaList.map(FuncParamList::representation).orElse("")
                + rightParenthesisToken.representation() + ' ' + block.representation();
    }

    @Override
    public String categoryCode() {
        return "<FuncDef>";
    }
}
