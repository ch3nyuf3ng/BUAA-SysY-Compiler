package lex.token;

import foundation.Position;
import lex.protocol.BasicTypeTokenType;
import lex.protocol.FuncTypeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record IntToken(
        String rawRepresentation,
        Position position
) implements TokenType, FuncTypeTokenType, BasicTypeTokenType {
    public IntToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public IntToken(Position position) {
        this("int", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "INTTK";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
