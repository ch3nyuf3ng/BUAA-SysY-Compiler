package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class MainFuncDefinition implements NonTerminatorType {
    private final IntToken intToken;
    private final MainToken mainToken;
    private final LeftParenthesisToken leftParenthesisToken;
    private final RightParenthesisToken rightParenthesisToken;
    private final Block block;

    private MainFuncDefinition(
            IntToken intToken,
            MainToken mainToken,
            LeftParenthesisToken leftParenthesisToken,
            RightParenthesisToken rightParenthesisToken,
            Block block
    ) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.leftParenthesisToken = leftParenthesisToken;
        this.rightParenthesisToken = rightParenthesisToken;
        this.block = block;
    }

    public static Optional<MainFuncDefinition> parse(LexerType lexer) {
        Logger.info("Matching <MainFuncDefinition>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var intToken = lexer.tryMatchAndConsumeTokenOf(IntToken.class);
            if (intToken.isEmpty()) break parse;

            final var mainToken = lexer.tryMatchAndConsumeTokenOf(MainToken.class);
            if (mainToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var block = Block.parse(lexer);
            if (block.isEmpty()) break parse;

            final var result = new MainFuncDefinition(
                    intToken.get(),
                    mainToken.get(),
                    leftParenthesisToken.get(),
                    rightParenthesisToken.get(),
                    block.get()
            );
            Logger.info("Matched <MainFuncDefinition>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <MainFuncDefinition>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }


    @Override
    public TokenType lastTerminator() {
        return block.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return intToken.detailedRepresentation() + mainToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation() + rightParenthesisToken.detailedRepresentation()
                + block.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return intToken.representation() + " " + mainToken.representation() + leftParenthesisToken.representation()
                + rightParenthesisToken.representation() + " " + block.representation();
    }

    @Override
    public String categoryCode() {
        return "<MainFuncDef>";
    }
}
