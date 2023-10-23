package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record IdentifierToken(String name, Position position) implements TokenType {
    public IdentifierToken {
        Objects.requireNonNull(name);
        Objects.requireNonNull(position);
    }

    @Override
    public String rawRepresentation() {
        return name();
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "IDENFR";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}