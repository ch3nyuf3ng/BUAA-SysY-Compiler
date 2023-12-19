import error.ErrorHandler;
import foundation.IO;
import lex.Lexer;
import parse.Parser;
import pcode.Interpreter;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;

import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        final var sourceCode = IO.simpleInput("testfile.txt");
        final var errorHandler = new ErrorHandler(true);
        final var lexer = new Lexer(errorHandler, sourceCode);
        final var parser = new Parser(errorHandler, lexer);
        final var possibleCompileUnit = parser.parse();
        if (possibleCompileUnit.isEmpty()) {
            throw new RuntimeException("Parse failed.");
        }
        final var compileUnit = possibleCompileUnit.get();
        final var symbolManager = new SymbolManager();
        final var pcodeList = new ArrayList<PcodeType>();
        compileUnit.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        if (errorHandler.hasError()) {
            IO.simpleOutput("error.txt", errorHandler.generateSimpleLog());
        } else {
            final var interpreter = new Interpreter(pcodeList, null);
            interpreter.run();
            final var result = interpreter.output();
            IO.simpleOutput("pcoderesult.txt", result);
        }
    }
}
