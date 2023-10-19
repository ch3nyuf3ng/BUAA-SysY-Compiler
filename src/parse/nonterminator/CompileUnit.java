package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import foundation.Logger;

import java.util.*;

public class CompileUnit implements NonTerminatorType {
    private final List<Declaration> declarationList;
    private final List<FuncDefinition> funcDefinitionList;
    private final MainFuncDefinition mainFuncDefinition;

    public CompileUnit(
            List<Declaration> declarationList,
            List<FuncDefinition> funcDefinitionList,
            MainFuncDefinition mainFuncDefinition
    ) {
        this.declarationList = Collections.unmodifiableList(declarationList);
        this.funcDefinitionList = Collections.unmodifiableList(funcDefinitionList);
        this.mainFuncDefinition = Objects.requireNonNull(mainFuncDefinition);
    }

    public static Optional<CompileUnit> parse(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        Logger.info("Matching <CompileUnit>");

        parse:
        {
            final List<Declaration> declarationList = new ArrayList<>();
            while (Declaration.matchBeginTokens(lexer)) {
                final var declaration = Declaration.parse(lexer);
                if (declaration.isEmpty()) break;
                declarationList.add(declaration.get());
            }

            final List<FuncDefinition> funcDefinitionList = new ArrayList<>();
            while (FuncDefinition.isMatchedBeginningTokens(lexer)) {
                final var funcDefinition = FuncDefinition.parse(lexer);
                if (funcDefinition.isEmpty()) break;
                funcDefinitionList.add(funcDefinition.get());
            }

            final var mainFuncDefinition = MainFuncDefinition.parse(lexer);
            if (mainFuncDefinition.isEmpty()) break parse;

            final var result = new CompileUnit(declarationList, funcDefinitionList, mainFuncDefinition.get());
            Logger.info("Matched <CompileUnit>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <CompileUnit>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public
    String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        funcDefinitionList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        stringBuilder.append(mainFuncDefinition.detailedRepresentation()).append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.representation()).append('\n'));
        stringBuilder.append('\n');
        funcDefinitionList.forEach(e -> stringBuilder.append(e.representation()).append("\n\n"));
        stringBuilder.append(mainFuncDefinition.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<CompUnit>";
    }

    @Override
    public TokenType lastTerminator() {
        return mainFuncDefinition.lastTerminator();
    }
}
