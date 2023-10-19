package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class Declaration implements NonTerminatorType, SelectionType {
    private final SelectionType declaration;

    public Declaration(SelectionType declaration) {
        this.declaration = Objects.requireNonNull(declaration);
    }

    public static boolean matchBeginTokens(LexerType lexer) {
        return VarDeclaration.isMatchedBeginTokens(lexer) || ConstDeclaration.matchBeginToken(lexer);
    }

    public static Optional<Declaration> parse(LexerType lexer) {
        Logger.info("Matching <Declaration>.");
        final var beginningPosition = lexer.beginningPosition();

        if (ConstDeclaration.matchBeginToken(lexer)) {
            final var constDeclaration = ConstDeclaration.parse(lexer);
            if (constDeclaration.isPresent()) {
                final var result = new Declaration(constDeclaration.get());
                Logger.info("Matched <Declaration>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (VarDeclaration.isMatchedBeginTokens(lexer)) {
            final var varDeclaration = VarDeclaration.parse(lexer);
            if (varDeclaration.isPresent()) {
                final var result = new Declaration(varDeclaration.get());
                Logger.info("Matched <Declaration>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <Declaration>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return declaration.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return declaration.detailedRepresentation();
    }

    @Override
    public String representation() {
        return declaration.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }
}
