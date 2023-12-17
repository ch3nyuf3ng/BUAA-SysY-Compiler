package tests.pcode;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.IO;
import foundation.Logger;
import lex.Lexer;
import parse.Parser;
import pcode.Interpreter;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;

import java.io.File;
import java.util.ArrayList;

public class PcodeTest {
    private final static String InputFilePath = "input";
    private final static String SourceCodeFilePath = "testfile";
    private final static String FormattedSourceCodeFilePath = "formattedcode";
    private final static String MyOutputFolderPath = "myoutput";
    private final static String PcodeFolderPath = "pcode";

    public static void main(String[] args) {
        int i;
        for (i = 1; i <= 15; i += 1) {
            Logger.debug("Test " + i, Logger.Category.INTERPRETER);
            final var sourceCode = IO.simpleInput(SourceCodeFilePath + File.separator + "testfile" + i + ".txt");
            final var input = IO.simpleInput(InputFilePath + File.separator + "input" + i + ".txt");
            final var errorHandler = new ErrorHandler(false);
            final var lexer = new Lexer(errorHandler, sourceCode);
            final var parser = new Parser(errorHandler, lexer);
            final var possibleCompileUnit = parser.parse();
            if (possibleCompileUnit.isEmpty()) {
                throw new RuntimeException();
            }
            final var compileUnit = possibleCompileUnit.get();
            IO.simpleOutputToFolder(FormattedSourceCodeFilePath, "code" + i + ".c", compileUnit.representation());
            final var symbolManager = new SymbolManager();
            final var pcodeList = new ArrayList<PcodeType>();
            try {
                compileUnit.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
            } catch (FatalErrorException e) {
                throw new RuntimeException(e);
            }
            final var pcodeBuilder = new StringBuilder();
            for (final var pcode : pcodeList) {
                final var pcodeToPrint = pcode.representation().replace("\n", "\\n");
                pcodeBuilder.append(pcodeToPrint).append('\n');
            }
            final var pcode = pcodeBuilder.toString();
            IO.simpleOutputToFolder(PcodeFolderPath, "pcode" + i + ".txt", pcode);
            final var interpreter = new Interpreter(pcodeList, input);
            interpreter.run();
            final var result = interpreter.output();
            IO.simpleOutputToFolder(MyOutputFolderPath, "output" + i + ".txt", result);
            Logger.writeLogFile();
        }
    }
}
