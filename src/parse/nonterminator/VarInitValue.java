package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftBraceToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.ArrayVarInitValue;
import parse.selections.ScalarVarInitValue;
import tests.foundations.Logger;

import java.util.Objects;
import java.util.Optional;

public class VarInitValue implements NonTerminatorType {
    private final SelectionType varInitVal;

    public VarInitValue(SelectionType varInitVal) {
        this.varInitVal = Objects.requireNonNull(varInitVal);
    }

    public static Optional<VarInitValue> parse(LexerType lexer) {
        Logger.info("Matching <VarInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(LeftBraceToken.class)) {
            final var arrayVarInitValue = ArrayVarInitValue.parse(lexer);
            if (arrayVarInitValue.isPresent()) {
                final var result = new VarInitValue(arrayVarInitValue.get());
                Logger.info("Matched <VarInitValue>: " + result.representation());
                return Optional.of(result);
            }
        } else {
            final var scalarVarInitValue = ScalarVarInitValue.parse(lexer);
            if (scalarVarInitValue.isPresent()) {
                final var result = new VarInitValue(scalarVarInitValue.get());
                Logger.info("Matched <VarInitValue>: " + result.representation());
                return Optional.of(result);
            }
        }

        Logger.info("Failed to match <VarInitValue>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        return varInitVal.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return varInitVal.representation();
    }

    @Override
    public String categoryCode() {
        return "<InitVal>";
    }

    @Override
    public TokenType lastTerminator() {
        return varInitVal.lastTerminator();
    }
}
