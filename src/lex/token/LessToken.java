package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record LessToken(
        String rawRepresentation, Position position
) implements TokenType, RelaitionalOperatorTokenType {
    public LessToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LessToken(Position position) {
        this("<", position);
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
        return rawRepresentation();
    }
}
