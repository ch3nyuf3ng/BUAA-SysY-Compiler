package lex.token;

import foundation.Position;
import lex.protocol.AdditiveTokenType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

import java.util.Objects;

public record PlusToken(
        String rawRepresentation, Position position
) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    public PlusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public PlusToken(Position position) {
        this("+", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "PLUS";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
