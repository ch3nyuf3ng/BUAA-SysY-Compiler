package tests.error;

import error.ErrorHandler;
import foundation.IO;
import foundation.Logger;
import lex.Lexer;
import parse.Parser;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;

import java.io.File;
import java.util.ArrayList;

public class ErrorHandlingTest {
    private final static String SourceCodeFilePath = "testfile";
    private final static String ErrorOutputFolderPath = "myoutput";

    public static void main(String[] args) {
        int i;
        for (i = 1; i <= 13; i += 1) {
            Logger.debug("Test " + i, Logger.Category.INTERPRETER);
            final var sourceCode = IO.simpleInput(SourceCodeFilePath + File.separator + "testfile" + i + ".txt");
            final var errorHandler = new ErrorHandler(true);
            final var lexer = new Lexer(errorHandler, sourceCode);
            final var parser = new Parser(errorHandler, lexer);
            final var possibleCompileUnit = parser.parse();
            if (possibleCompileUnit.isEmpty()) {
                throw new RuntimeException();
            }
            final var compileUnit = possibleCompileUnit.get();
            final var symbolManager = new SymbolManager();
            final var pcodeList = new ArrayList<PcodeType>();
            compileUnit.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
            IO.simpleOutputToFolder(ErrorOutputFolderPath, "error" + i + ".txt", errorHandler.generateSimpleLog());
            Logger.writeLogFile();
        }
    }
}
