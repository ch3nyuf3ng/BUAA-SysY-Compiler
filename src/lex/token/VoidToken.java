package lex.token;

import foundation.Position;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record VoidToken(String rawRepresentation, Position position) implements TokenType, FuncTypeTokenType {
    public VoidToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public VoidToken(Position position) {
        this("void", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "VOIDTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
