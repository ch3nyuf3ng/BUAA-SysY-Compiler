package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class BlockItem implements NonTerminatorType {
    private final SelectionType blockItem;

    private BlockItem(SelectionType blockItem) {
        this.blockItem = blockItem;
    }

    public static Optional<BlockItem> parse(LexerType lexer) {
        Logger.info("Matching <BlockItem>.");
        final var beginningPosition = lexer.beginningPosition();

        if (Declaration.matchBeginTokens(lexer)) {
            final var declaration = Declaration.parse(lexer);
            if (declaration.isPresent()) {
                final var result = new BlockItem(declaration.get());
                Logger.info("Matched <BlockItem>:\n" + result.representation());
                return Optional.of(result);
            }
        } else {
            final var statement = Statement.parse(lexer);
            if (statement.isPresent()) {
                final var result = new BlockItem(statement.get());
                Logger.info("Matched <BlockItem>:\n" + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <BlockItem>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return blockItem.detailedRepresentation();
    }

    @Override
    public String representation() {
        return blockItem.representation();
    }

    @Override
    public String categoryCode() {
        return "<BlockItem>";
    }

    @Override
    public TokenType lastTerminator() {
        return null;
    }
}
