package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class IdentifierToken implements TokenType {
    final private String name;
    final private Position position;

    public IdentifierToken(String name, Position position) {
        this.name = Objects.requireNonNull(name);
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierToken that = (IdentifierToken) o;
        return Objects.equals(name, that.name) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }

    public String name() {
        return name;
    }

    @Override
    public Position position() {
        return position;
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
        return name;
    }
}