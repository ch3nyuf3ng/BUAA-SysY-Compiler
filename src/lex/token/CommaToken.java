package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record CommaToken(String rawRepresentation, Position position) implements TokenType {
    public CommaToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public CommaToken(Position position) {
        this(",", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "COMMA";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
