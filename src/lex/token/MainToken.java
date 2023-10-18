package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public record MainToken(Position position) implements TokenType {
    public MainToken(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainToken mainToken = (MainToken) o;
        return Objects.equals(position, mainToken.position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MAINTK";
    }

    @Override
    public String representation() {
        return "main";
    }
}
