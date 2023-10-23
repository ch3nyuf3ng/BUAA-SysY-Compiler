package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record LessOrEqualToken(
        String rawRepresentation, Position position
) implements TokenType, RelaitionalOperatorTokenType {
    public LessOrEqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LessOrEqualToken(Position position) {
        this("<=", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "LEQ";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
