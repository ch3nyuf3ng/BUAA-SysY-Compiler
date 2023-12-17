package tests.error;

import error.ErrorHandler;
import foundation.Pair;
import foundation.Tester;
import lex.Lexer;
import parse.Parser;

import java.util.ArrayList;

public class ErrorHandlingTest {
    private static String process(String sourceCode) {
        final var errorHandler = new ErrorHandler(true);
        final var lexer = new Lexer(errorHandler, sourceCode);
        final var parser = new Parser(errorHandler, lexer);
        parser.parse();
        return errorHandler.generateSimpleLog();
    }

    public static void main(String[] args) {
        final var inputFolderPath = "testfile";
        final var testOutputFolderPath = "myoutput";
        final var standardOutputFolderPath = "stdoutput";
        final var differencesLogPath = "diff";
        final var inputOutputFilenameList = new ArrayList<Pair<String, String>>();
        inputOutputFilenameList.add(new Pair<>("illegalCharacter.txt", "illegalCharacter.txt"));

        Tester.test(
                inputFolderPath,
                testOutputFolderPath,
                standardOutputFolderPath,
                differencesLogPath,
                inputOutputFilenameList,
                ErrorHandlingTest::process
        );
    }
}
