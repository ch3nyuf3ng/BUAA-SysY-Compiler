package foundation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IO {
    public static String simpleInput(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String simpleInputFromFolder(String folderPath, String filename) {
        return simpleInput(folderPath + File.separator + filename);
    }

    public static void simpleOutputToFolder(String folderPath, String filename, String content) {
        try {
            final File folder = new File(folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                System.out.println("Failed to create the output folder.");
            }
            final var file = new File(folder, filename);
            final var fileWriter = new FileWriter(file);
            final var bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void simpleOutput(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
