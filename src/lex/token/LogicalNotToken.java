package lex.token;

import foundation.Position;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

import java.util.Objects;

public record LogicalNotToken(
        String rawRepresentation, Position position
) implements TokenType, UnaryOperatorTokenType {
    public LogicalNotToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public LogicalNotToken(Position position) {
        this("!", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "NOT";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
