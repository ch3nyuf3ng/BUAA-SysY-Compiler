package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.ConstToken;
import lex.token.IntToken;
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

        final var optionalDeclarationBeginningToken = lexer.currentToken()
                .filter(t -> t instanceof ConstToken || t instanceof IntToken );
        if (optionalDeclarationBeginningToken.isPresent()) {
            final var optionalDeclaration = Declaration.parse(lexer);
            if (optionalDeclaration.isPresent()) {
                final var declaration = optionalDeclaration.get();
                Logger.info("Matched <BlockItem>.");
                return Optional.of(new BlockItem(declaration));
            }
        } else {
            final var optionalStatement = Statement.parse(lexer);
            if (optionalStatement.isPresent()) {
                final var statement = optionalStatement.get();
                final var result = new BlockItem(statement);
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
