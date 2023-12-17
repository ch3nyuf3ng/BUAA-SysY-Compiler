package terminators.nontokens;

import foundation.Position;
import terminators.protocols.NonTokenType;

import java.util.Objects;

public record CommentSingleLine(
        String content,
        Position beginningPosition,
        Position endingPosition
) implements NonTokenType {
    public CommentSingleLine {
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
        return "SCOMMENT";
    }

    @Override
    public String representation() {
        return "//" + content;
    }
}
