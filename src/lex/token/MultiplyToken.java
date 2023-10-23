package lex.token;

import foundation.Position;
import lex.protocol.MultiplicativeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record MultiplyToken(
        String rawRepresentation, Position position
) implements TokenType, MultiplicativeTokenType {
    public MultiplyToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public MultiplyToken(Position position) {
        this("*", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MULT";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
