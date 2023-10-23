package lex.token;

import foundation.Position;
import lex.protocol.MultiplicativeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record ModulusToken(
        String rawRepresentation, Position position
) implements TokenType, MultiplicativeTokenType {
    public ModulusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public ModulusToken(Position position) {
        this("%", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "MOD";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
