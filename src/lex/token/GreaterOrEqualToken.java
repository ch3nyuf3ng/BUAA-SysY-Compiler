package lex.token;

import foundation.Position;
import lex.protocol.RelaitionalOperatorTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record GreaterOrEqualToken(
        String rawRepresentation, Position position
) implements TokenType, RelaitionalOperatorTokenType {
    public GreaterOrEqualToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public GreaterOrEqualToken(Position position) {
        this(">=", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "GEQ";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
