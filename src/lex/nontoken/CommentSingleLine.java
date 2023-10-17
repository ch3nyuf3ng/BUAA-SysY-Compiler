package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

public record CommentSingleLine(
        String content,
        Position position
) implements NonTokenType {
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
