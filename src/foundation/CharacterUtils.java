package foundation;

public class CharacterUtils {
    public static boolean isLetter(char character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    public static boolean isDigit(char character) {
        return (character >= '0' && character <= '9');
    }

    public static boolean isUnderline(char character) {
        return character == '_';
    }

    public static boolean isStar(char character) {
        return character == '*';
    }

    public static boolean isSlash(char character) {
        return character == '/';
    }

    public static boolean isGeneralWhitespace(char character) {
        return character == ' ' || character == '\t' || character == '\f' || character == '\r' || character == '\n';
    }

    public static boolean isLF(char first) {
        return first == '\n';
    }

    public static boolean isCR(char first, char second) {
        return first == '\r' && second != '\n';
    }

    public static boolean isCRLF(char first, char second) {
        return first == '\r' && second == '\n';
    }
}
