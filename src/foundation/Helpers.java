package foundation;

import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Helpers {
    public static List<String> findDifferingLines(String str1, String str2) {
        final var lines1 = str1.split("[\\r\\n]+");
        final var lines2 = str2.split("[\\r\\n]+");

        final var differingLines = new ArrayList<String>();

        final var minLength = Math.min(lines1.length, lines2.length);
        for (var i = 0; i < minLength; i++) {
            if (!lines1[i].equals(lines2[i])) {
                differingLines.add("Line " + (i + 1) + ":\n" + lines1[i] + "\n" + lines2[i] + "\n");
            }
        }

        // 如果行数不相同，将剩余的行添加到不一致的列表中
        for (int i = minLength; i < lines1.length; i++) {
            differingLines.add("Line " + (i + 1) + ":\n" + lines1[i] + "\n");
        }
        for (int i = minLength; i < lines2.length; i++) {
            differingLines.add("Line " + (i + 1) + ":\n" + lines2[i] + "\n");
        }

        return differingLines;
    }

    public static String readLine(String text, Position position) {
        final var stringBuilder = new StringBuilder();
        var currentIndex = position.characterIndex() - position.columnNumber() + 1;
        while (!CharacterUtils.isCR(text.charAt(currentIndex))
                && !CharacterUtils.isLF(text.charAt(currentIndex))
        ) {
            stringBuilder.append(text.charAt(currentIndex));
            currentIndex += 1;
        }
        return stringBuilder.toString();
    }

    public static int digitsCountOf(int integer) {
        return String.valueOf(integer).length();
    }

    public static int formatDescriptorCount(String string) {
        final var length = string.length();
        var count = 0;
        for (var i = 0; i < length - 1; ) {
            final var firstChar = string.charAt(i);
            final var secondChar = string.charAt(i + 1);
            if (firstChar == '%' && secondChar == 'd') {
                count += 1;
                i += 2;
            } else {
                i += 1;
            }
        }
        return count;
    }

    public static int lineNumberOf(TokenType token) {
        return token.beginningPosition().lineNumber();
    }
}
