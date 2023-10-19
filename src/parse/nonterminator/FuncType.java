package parse.nonterminator;

import lex.protocol.FuncTypeTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.Objects;
import java.util.Optional;

public class FuncType implements NonTerminatorType {
    private final FuncTypeTokenType funcType;

    public FuncType(FuncTypeTokenType funcType) {
        this.funcType = Objects.requireNonNull(funcType);
    }

    public static Optional<FuncType> parse(LexerType lexer) {
        Logger.info("Matching <FuncType>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var funcType = lexer.tryMatchAndConsumeTokenOf(FuncTypeTokenType.class);
            if (funcType.isEmpty()) break parse;

            final var result = new FuncType(funcType.get());
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
