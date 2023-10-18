package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

import java.util.Objects;

public class Unknown implements NonTokenType {
    private final String rawRepresentation;
    private final Position position;

    public Unknown(String rawRepresentation, Position position) {
        this.rawRepresentation = rawRepresentation;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unknown unknown = (Unknown) o;
        return Objects.equals(rawRepresentation, unknown.rawRepresentation) && Objects.equals(
                position,
                unknown.position
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawRepresentation, position);
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
        return "UNKNOWN";
    }

    @Override
    public String representation() {
        return rawRepresentation;
    }
}
