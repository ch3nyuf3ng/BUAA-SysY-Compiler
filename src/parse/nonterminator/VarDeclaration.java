package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.BasicTypeTokenType;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.IdentifierToken;
import lex.token.LeftParenthesisToken;
import lex.token.SemicolonToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VarDeclaration implements NonTerminatorType, SelectionType {
    private final BasicType basicType;
    private final VarDefinition firstVarDefinition;
    private final List<Pair<CommaToken, VarDefinition>> additionalVariableDefinitionList;
    private final Optional<SemicolonToken> semicolonToken;

    private VarDeclaration(
            BasicType basicType,
            VarDefinition firstVarDefinition,
            List<Pair<CommaToken, VarDefinition>> additionalVariableDefinitionList,
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

            final var additionalVarDefinitionList = new ArrayList<Pair<CommaToken, VarDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var varDefinition = VarDefinition.parse(lexer);
                if (varDefinition.isEmpty()) break parse;

                additionalVarDefinitionList.add(new Pair<>(commaToken.get(), varDefinition.get()));
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
        return basicType.detailedRepresentation()
                + RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                        firstVarDefinition, additionalVariableDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                        firstVarDefinition, additionalVariableDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::representation).orElse("");
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
            final var lastNonTerminator = additionalVariableDefinitionList.get(lastIndex).second();
            return lastNonTerminator.lastTerminator();
        }
        return firstVarDefinition.lastTerminator();
    }
}
