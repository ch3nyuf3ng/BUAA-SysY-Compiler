package lex.token;

import foundation.Position;
import lex.protocol.TokenType;

import java.util.Objects;

public class LiteralFormatStringToken implements TokenType {
    static public boolean isLegalCharacter(char character) {
        final var ascii = (int) character;
        return ascii == 32 || ascii == 33 || 40 <= ascii && ascii <= 126;
    }

    private final String content;
    private final Position position;

    public LiteralFormatStringToken(String content, Position position) {
        this.content = Objects.requireNonNull(content);
        this.position = Objects.requireNonNull(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralFormatStringToken that = (LiteralFormatStringToken) o;
        return Objects.equals(content, that.content) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, position);
    }

    public String content() {
        return content;
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
        return "STRCON";
    }

    @Override
    public String representation() {
        return "\"" + content + "\"";
    }
}