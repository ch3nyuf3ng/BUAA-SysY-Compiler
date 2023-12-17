package tests.parser;

import error.ErrorHandler;
import foundation.IO;
import lex.Lexer;
import nonterminators.CompileUnit;
import parse.Parser;
import foundation.Logger;

import java.io.File;

public class ParserTest {
    private final static String InputFilePath = "testfile";
    private final static String MyOutputFolderPath = "myoutput";

    public static void main(String[] args) {
        for (int i = 1; i <= 15; i += 1) {
            Logger.debug("Test " + i, Logger.Category.TEST);
            final var sourceCode = IO.simpleInput(InputFilePath + File.separator + "testfile" + i + ".txt");
            final var errorHandler = new ErrorHandler(false);
            final var lexer = new Lexer(errorHandler, sourceCode);
            final var parser = new Parser(errorHandler, lexer);
            final var compileUnit = parser.parse();
            final var result = compileUnit.map(CompileUnit::detailedRepresentation).orElse("Failed.");
            IO.simpleOutputToFolder(MyOutputFolderPath, "output" + i + ".txt", result);
            Logger.writeLogFile();
        }
    }
}
