package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompileUnit implements NonTerminatorType {
    private final List<Declaration> declarationList;
    private final List<FuncDefinition> funcDefinitionList;
    private final MainFuncDefinition mainFuncDefinition;

    private CompileUnit(
            List<Declaration> declarationList,
            List<FuncDefinition> funcDefinitionList,
            MainFuncDefinition mainFuncDefinition
    ) {
        this.declarationList = declarationList;
        this.funcDefinitionList = funcDefinitionList;
        this.mainFuncDefinition = mainFuncDefinition;
    }

    public static Optional<CompileUnit> parse(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        Logger.info("Matching <CompileUnit>");

        parse: {
            final List<Declaration> declarationList = new ArrayList<>();
            while (Declaration.matchBeginTokens(lexer)) {
                final var optDeclaration = Declaration.parse(lexer);
                if (optDeclaration.isEmpty()) break;
                final var declaration = optDeclaration.get();
                declarationList.add(declaration);
            }

            final List<FuncDefinition> funcDefinitionList = new ArrayList<>();
            while (FuncDefinition.matchBeginTokens(lexer)) {
                final var optionalFuncDefinition = FuncDefinition.parse(lexer);
                if (optionalFuncDefinition.isEmpty()) break;
                final var funcDefinition = optionalFuncDefinition.get();
                funcDefinitionList.add(funcDefinition);
            }

            final var optionalMainFuncDefinition = MainFuncDefinition.parse(lexer);
            if (optionalMainFuncDefinition.isEmpty()) break parse;
            final var mainFuncDefinition = optionalMainFuncDefinition.get();

            final var result = new CompileUnit(
                    declarationList,
                    funcDefinitionList,
                    mainFuncDefinition
            );
            Logger.info("Matched <CompileUnit>:\n" + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <CompileUnit>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        funcDefinitionList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        stringBuilder
                .append(mainFuncDefinition.detailedRepresentation())
                .append(categoryCode());
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.representation()).append('\n'));
        funcDefinitionList.forEach(e -> stringBuilder.append(e.representation()).append('\n'));
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
