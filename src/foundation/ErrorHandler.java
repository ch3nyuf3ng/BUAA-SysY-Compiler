package foundation;

import error.protocol.SimpleErrorType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorHandler {
    private final static String ErrorFilePath = "error.txt";

    public static void outputSimpleError(SimpleErrorType simpleError) {
        try {
            final var writer = new BufferedWriter(new FileWriter(ErrorFilePath));
            writer.write(simpleError.simpleErrorMessage() + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
