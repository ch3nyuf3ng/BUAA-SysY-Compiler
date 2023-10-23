package lex.token;

import foundation.Position;
import lex.protocol.AdditiveTokenType;
import lex.protocol.TokenType;
import lex.protocol.UnaryOperatorTokenType;

import java.util.Objects;

public record MinusToken(
        String rawRepresentation, Position position
) implements TokenType, AdditiveTokenType, UnaryOperatorTokenType {
    public MinusToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public MinusToken(Position position) {
        this("-", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MINU";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
