package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.IntToken;
import lex.token.VoidToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.Optional;

public class FuncType implements NonTerminatorType {
    private final TokenType funcType;

    private FuncType(TokenType funcType) {
        this.funcType = funcType;
    }

    public static Optional<FuncType> parse(LexerType lexer) {
        Logger.info("Matching <FuncType>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalFuncType = lexer.currentToken()
                    .filter(t -> t instanceof IntToken || t instanceof VoidToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return t;
                    });
            if (optionalFuncType.isEmpty()) break parse;
            final var funcType = optionalFuncType.get();

            final var result = new FuncType(funcType);
            Logger.info("Matched <FuncType>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncType>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return funcType;
    }

    @Override
    public String detailedRepresentation() {
        return funcType.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return funcType.representation();
    }

    @Override
    public String categoryCode() {
        return "<FuncType>";
    }
}
