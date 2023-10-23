package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

import java.util.Objects;

public record CommentSingleLine(String content, Position position) implements NonTokenType {
    public CommentSingleLine {
        Objects.requireNonNull(content);
        Objects.requireNonNull(position);
    }

    @Override
    public String rawRepresentation() {
        return representation();
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
        return "//" + content;
    }
}
