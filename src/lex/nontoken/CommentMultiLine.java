package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

import java.util.Objects;

public record CommentMultiLine(String content, Position position) implements NonTokenType {
    public CommentMultiLine {
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
        return "MCOMMENT";
    }

    @Override
    public String representation() {
        return "/*" + content + "*/";
    }
}
