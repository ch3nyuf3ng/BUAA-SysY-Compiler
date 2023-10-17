package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftBraceToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.ArrayConstInitValue;
import parse.selections.ScalarConstInitValue;
import tests.foundations.Logger;

import java.util.Optional;

public class ConstInitValue implements NonTerminatorType {
    private final SelectionType constInitValue;

    private ConstInitValue(SelectionType constInitValue) {
        this.constInitValue = constInitValue;
    }

    public static Optional<ConstInitValue> parse(LexerType lexer) {
        Logger.info("Matching <ConstInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        final var optionalLeftBraceToken = lexer.currentToken()
                .filter(t -> t instanceof LeftBraceToken);
        if (optionalLeftBraceToken.isPresent()) {
            final var optionalArrayConstInitValue = ArrayConstInitValue.parse(lexer);
            if (optionalArrayConstInitValue.isPresent()) {
                final var arrayConstInitValue = optionalArrayConstInitValue.get();
                final var result = new ConstInitValue(arrayConstInitValue);
                Logger.info("Matched <ConstInitValue>: " + result.representation());
                return Optional.of(result);
            }
        } else {
            final var optionalScalarConstInitValue = ScalarConstInitValue.parse(lexer);
            if (optionalScalarConstInitValue.isPresent()) {
                final var scalarConstInitValue = optionalScalarConstInitValue.get();
                final var result = new ConstInitValue(scalarConstInitValue);
                Logger.info("Matched <ConstInitValue>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <ConstInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        return constInitValue.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return constInitValue.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return constInitValue.representation();
    }

    @Override
    public String categoryCode() {
        return "<ConstInitVal>";
    }
}
