package lex.token;

import foundation.Position;
import lex.protocol.MultiplicativeTokenType;
import lex.protocol.TokenType;

import java.util.Objects;

public record DivideToken(String rawRepresentation, Position position)
        implements TokenType, MultiplicativeTokenType
{
    public DivideToken {
        Objects.requireNonNull(rawRepresentation);
        Objects.requireNonNull(position);
    }

    public DivideToken(Position position) {
        this("/", position);
    }

    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "DIV";
    }

    @Override
    public String representation() {
        return rawRepresentation();
    }
}
