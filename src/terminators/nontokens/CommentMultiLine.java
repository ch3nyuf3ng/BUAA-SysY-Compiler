package terminators.nontokens;

import foundation.Position;
import terminators.protocols.NonTokenType;

import java.util.Objects;

public record CommentMultiLine(
        String content,
        Position beginningPosition,
        Position endingPosition
) implements NonTokenType {
    public CommentMultiLine {
        Objects.requireNonNull(content);
        Objects.requireNonNull(beginningPosition);
        Objects.requireNonNull(endingPosition);
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
