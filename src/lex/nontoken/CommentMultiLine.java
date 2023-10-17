package lex.nontoken;

import foundation.Position;
import lex.protocol.NonTokenType;

public record CommentMultiLine(
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
        return "MCOMMENT";
    }

    @Override
    public String representation() {
        return content;
    }
}
