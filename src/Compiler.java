import foundation.IO;
import lex.Lexer;
import parse.Parser;

public class Compiler {
    @SuppressWarnings("SpellCheckingInspection")
    private final static String InputFilePath = "testfile.txt";
    private final static String OutputFilePath = "output.txt";

    @SuppressWarnings("CommentedOutCode")
    public static void main(String[] args) {
        final var sourceCode = IO.readStringFrom(InputFilePath);
        final var lexer = new Lexer(sourceCode);
/*
        // Lexer Results.
        final var result = new StringBuilder();
        while (lexer.currentToken().isPresent()) {
            result.append(lexer.currentToken().get().detailedRepresentation());
            lexer.consumeToken();
        }
        IO.outputResult(".", OutputFilePath, result.toString());
*/
        // Parser Results
        final var parser = new Parser(lexer);
        IO.outputResult(".", OutputFilePath, parser.customString());
    }
}
