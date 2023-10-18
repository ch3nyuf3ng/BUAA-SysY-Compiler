package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record LessToken(Position position) implements TokenType, RelaitionalOperatorTokenType {
    public LessToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "LSS";
    }

    @Override
    public String representation() {
        return "<";
    }
}
