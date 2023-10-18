package lex.token;

import foundation.Position;
import lex.protocol.AdditiveTokenType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

import java.util.Objects;

public record PlusToken(Position position) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    public PlusToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlusToken plusToken = (PlusToken) o;
        return Objects.equals(position, plusToken.position);
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
        return "+";
    }
}
