package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record CommaToken(Position position) implements TokenType {
    public CommaToken(Position position) {
        this.position = Objects.requireNonNull(position);
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
        return ",";
    }
}
