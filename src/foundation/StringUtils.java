package foundation;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<String> findDifferingLines(String str1, String str2) {
        String[] lines1 = str1.split("[\\r\\n]+");
        String[] lines2 = str2.split("[\\r\\n]+");

        List<String> differingLines = new ArrayList<>();

        int minLength = Math.min(lines1.length, lines2.length);
        for (int i = 0; i < minLength; i++) {
            if (!lines1[i].equals(lines2[i])) {
                differingLines.add("行 " + (i + 1) + ": " + lines1[i] + " | " + lines2[i]);
            }
        }

        // 如果行数不相同，将剩余的行添加到不一致的列表中
        for (int i = minLength; i < lines1.length; i++) {
            differingLines.add("行 " + (i + 1) + ": " + lines1[i] + " | ");
        }
        for (int i = minLength; i < lines2.length; i++) {
            differingLines.add("行 " + (i + 1) + ":  | " + lines2[i]);
        }

        return differingLines;
    }
}
