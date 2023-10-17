package tests.lexer;

import lex.Lexer;
import foundation.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("SpellCheckingInspection")
public class LexerTester {
    private final static String InputFilePath = "testfile";
    private final static String MyOutputFolderPath = "myoutput";
    private final static String StandardOutputFolderPath = "stdoutput";

    public static void main(String[] args) {
        for (int i = 1; i <= 1; i += 1) {
            final var sourceCode = IO.readStringFrom(InputFilePath + File.separator + "testfile" + i + ".txt");
            final var lexer = new Lexer(sourceCode);
            final var resultBuilder = new StringBuilder();
            while (lexer.currentToken().isPresent()) {
                resultBuilder.append(lexer.currentToken().get().detailedRepresentation());
                lexer.consumeToken();
            }
            IO.outputResult(MyOutputFolderPath, "output" + i + ".txt", resultBuilder.toString());
            compareResults(i);
        }
    }

    private static void compareResults(int i) {
        try {
            final var outputFilename = "output" + i + ".txt";
            final var myOutput = Files.readString(Path.of(MyOutputFolderPath + File.separator + outputFilename));
            final var standardOutput = Files.readString(Path.of(StandardOutputFolderPath + File.separator + outputFilename));
            if (!myOutput.equals(standardOutput)) {
                System.out.println("The " + i + "th file is not same as the standard output.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
