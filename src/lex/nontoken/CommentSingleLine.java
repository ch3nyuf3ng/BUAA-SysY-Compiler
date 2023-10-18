package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

import java.util.Objects;

public class CommentSingleLine implements NonTokenType {
    private final String content;
    private final Position position;

    public CommentSingleLine(String content, Position position) {
        this.content = Objects.requireNonNull(content);
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentSingleLine that = (CommentSingleLine) o;
        return Objects.equals(content, that.content) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, position);
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
        return "SCOMMENT";
    }

    @Override
    public String representation() {
        return content;
    }
}
