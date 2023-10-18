package parse.nonterminator;

import lex.protocol.BasicTypeTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.*;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VarDeclaration implements NonTerminatorType, SelectionType {
    private final BasicType basicType;
    private final VarDefinition firstVarDefinition;
    private final List<CommaWith<VarDefinition>> additionalVariableDefinitionList;
    private final Optional<SemicolonToken> semicolonToken;

    private VarDeclaration(
            BasicType basicType,
            VarDefinition firstVarDefinition,
            List<CommaWith<VarDefinition>> additionalVariableDefinitionList,
            Optional<SemicolonToken> semicolonToken
    ) {
        this.basicType = basicType;
        this.firstVarDefinition = firstVarDefinition;
        this.additionalVariableDefinitionList = Collections.unmodifiableList(additionalVariableDefinitionList);
        this.semicolonToken = semicolonToken;
    }

    public static boolean isMatchedBeginTokens(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        final var intermediate = lexer.tryMatchAndConsumeTokenOf(BasicTypeTokenType.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class));
        lexer.resetPosition(beginningPosition);
        return intermediate.isPresent() && !lexer.isMatchedTokenOf(LeftParenthesisToken.class);
    }

    public static Optional<VarDeclaration> parse(LexerType lexer) {
        Logger.info("Matching <VarDeclaration>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var basicType = BasicType.parse(lexer);
            if (basicType.isEmpty()) break parse;

            final var firstVarDefinition = VarDefinition.parse(lexer);
            if (firstVarDefinition.isEmpty()) break parse;

            final var additionalVarDefinitionList = new ArrayList<CommaWith<VarDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var varDefinition = VarDefinition.parse(lexer);
                if (varDefinition.isEmpty()) break parse;

                additionalVarDefinitionList.add(new CommaWith<>(commaToken.get(), varDefinition.get()));
            }

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            if (semicolonToken.isEmpty()) {
                if (lexer.isMatchedTokenOf(LeftParenthesisToken.class)) break parse;
                Logger.warn("Tolerated a semicolon missing.");
            } // Avoid Matching FuncDef.

            final var result = new VarDeclaration(basicType.get(),
                    firstVarDefinition.get(),
                    additionalVarDefinitionList,
                    semicolonToken
            );
            Logger.info("Matched <VarDeclaration>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <VarDeclaration>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(basicType.detailedRepresentation()).append(firstVarDefinition.detailedRepresentation());
        additionalVariableDefinitionList.forEach(i -> stringBuilder.append(i.commaToken().detailedRepresentation())
                .append(i.entity().detailedRepresentation()));
        semicolonToken.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(basicType.representation()).append(' ').append(firstVarDefinition.representation());
        additionalVariableDefinitionList.forEach(i -> stringBuilder.append(i.commaToken().representation()).append(' ')
                .append(i.entity().representation()));
        semicolonToken.ifPresent(t -> stringBuilder.append(t.representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<VarDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        if (!additionalVariableDefinitionList.isEmpty()) {
            final var lastIndex = additionalVariableDefinitionList.size() - 1;
            final var lastNonTerminator = additionalVariableDefinitionList.get(lastIndex).entity();
            return lastNonTerminator.lastTerminator();
        }
        return firstVarDefinition.lastTerminator();
    }
}
