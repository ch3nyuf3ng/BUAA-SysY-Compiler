package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.Optional;

public class Declaration implements NonTerminatorType, SelectionType {
    private final SelectionType declaration;

    private Declaration(SelectionType declaration) {
        this.declaration = declaration;
    }

    public static boolean matchBeginTokens(LexerType lexer) {
        return VarDeclaration.matchBeginTokens(lexer) || ConstDeclaration.matchBeginToken(lexer);
    }

    public static Optional<Declaration> parse(LexerType lexer) {
        Logger.info("Matching <Declaration>.");
        final var beginningPosition = lexer.beginningPosition();

        if (ConstDeclaration.matchBeginToken(lexer)) {
            final var optionalConstDeclaration = ConstDeclaration.parse(lexer);
            if (optionalConstDeclaration.isPresent()) {
                final var constDeclaration = optionalConstDeclaration.get();
                final var result = new Declaration(constDeclaration);
                Logger.info("Matched <Declaration>: " + result.representation());
                return Optional.of(result);
            }
        }

        if (VarDeclaration.matchBeginTokens(lexer)) {
            final var optionalVarDeclaration = VarDeclaration.parse(lexer);
            if (optionalVarDeclaration.isPresent()) {
                final var varDeclaration = optionalVarDeclaration.get();
                final var result = new Declaration(varDeclaration);
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
