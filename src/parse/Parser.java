package parse;

import lex.protocol.LexerType;
import parse.nonterminator.CompileUnit;


public class Parser {
    private final CompileUnit compileUnit;

    public Parser(LexerType lexer) {
        this.compileUnit = CompileUnit.parse(lexer).orElse(null);
    }

    public String customString() {
        if (compileUnit != null) {
            return compileUnit.detailedRepresentation();
        }
        return "Failed.";
    }
}
