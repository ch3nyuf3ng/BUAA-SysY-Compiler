package tests.parser;

import foundation.IO;
import lex.Lexer;
import parse.Parser;
import tests.foundations.Logger;

import java.io.File;

public class ParserTest {
    private final static String InputFilePath = "testfile";
    private final static String MyOutputFolderPath = "myoutput";

    public static void main(String[] args) {
        for (int i = 1; i <= 15; i += 1) {
            Logger.info("Test " + i);
            final var sourceCode = IO.readStringFrom(InputFilePath + File.separator + "testfile" + i + ".txt");
            final var lexer = new Lexer(sourceCode);
            final var parser = new Parser(lexer);
            final var result = parser.customString();
            IO.outputResult(MyOutputFolderPath, "output" + i + ".txt", result);
            Logger.writeLogFile();
        }
    }
}
