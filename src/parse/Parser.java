package parse;

import lex.protocol.LexerType;
import parse.nonterminator.CompileUnit;

import java.util.Optional;


public class Parser {
    private final Optional<CompileUnit> compileUnit;

    public Parser(LexerType lexer) {
        this.compileUnit = CompileUnit.parse(lexer);
    }

    public String customString() {
        return compileUnit.map(CompileUnit::detailedRepresentation).orElse("Failed.");
    }
}
