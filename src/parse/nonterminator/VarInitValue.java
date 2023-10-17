package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.LeftBraceToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.selections.ArrayVarInitValue;
import parse.selections.ScalarVarInitValue;
import tests.foundations.Logger;

import java.util.Optional;

public class VarInitValue implements NonTerminatorType {
    private final SelectionType varInitVal;

    private VarInitValue(SelectionType varInitVal) {
        this.varInitVal = varInitVal;
    }

    public static Optional<VarInitValue> parse(LexerType lexer) {
        Logger.info("Matching <VarInitValue>.");
        final var beginningPosition = lexer.beginningPosition();

        final var optionalLeftBraceToken = lexer.currentToken()
                .filter(t -> t instanceof LeftBraceToken);
        if (optionalLeftBraceToken.isPresent()) {
            final var optionalArrayVarInitValue = ArrayVarInitValue.parse(lexer);
            if (optionalArrayVarInitValue.isPresent()) {
                final var arrayVarInitValue = optionalArrayVarInitValue.get();
                final var result = new VarInitValue(arrayVarInitValue);
                Logger.info("Matched <VarInitValue>: " + result.representation());
                return Optional.of(result);
            }
        } else {
            final var optionalScalarVarInitValue = ScalarVarInitValue.parse(lexer);
            if (optionalScalarVarInitValue.isPresent()) {
                final var scalarVarInitValue = optionalScalarVarInitValue.get();
                final var result = new VarInitValue(scalarVarInitValue);
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
