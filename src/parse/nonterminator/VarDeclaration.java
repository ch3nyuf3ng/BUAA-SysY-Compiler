package parse.nonterminator;

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
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private VarDeclaration(
            BasicType basicType,
            VarDefinition firstVarDefinition,
            List<CommaWith<VarDefinition>> additionalVariableDefinitionList,
            Optional<SemicolonToken> optionalSemicolonToken
    ) {
        this.basicType = basicType;
        this.firstVarDefinition = firstVarDefinition;
        this.additionalVariableDefinitionList = Collections.unmodifiableList(additionalVariableDefinitionList);
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static boolean matchBeginTokens(LexerType lexer) {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.currentToken()
                .filter(t -> t instanceof IntToken)
                .flatMap(t -> {
                    lexer.consumeToken();
                    return lexer.currentToken();
                })
                .filter(t -> t instanceof IdentifierToken)
                .flatMap(t -> {
                    lexer.consumeToken();
                    return lexer.currentToken();
                })
                .filter(t -> !(t instanceof LeftParenthesisToken)).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    public static Optional<VarDeclaration> parse(LexerType lexer) {
        Logger.info("Matching <VarDeclaration>.");
        final var beginningPosition = lexer.beginningPosition();

        parse: {
            final var optionalBasicType = BasicType.parse(lexer);
            if (optionalBasicType.isEmpty()) break parse;
            final var basicType = optionalBasicType.get();

            final var optionalFirstVarDefinition = VarDefinition.parse(lexer);
            if (optionalFirstVarDefinition.isEmpty()) break parse;
            final var firstVarDefinition = optionalFirstVarDefinition.get();

            final var additionalVarDefinitionList = new ArrayList<CommaWith<VarDefinition>>();
            while (true) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalVarDefinition = VarDefinition.parse(lexer);
                if (optionalVarDefinition.isEmpty()) break parse;
                final var varDefinition = optionalVarDefinition.get();

                additionalVarDefinitionList.add(new CommaWith<>(commaToken, varDefinition));
            }

            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            final var optionalLeftParenthesis = lexer.currentToken()
                    .filter(t -> t instanceof LeftParenthesisToken);
            if (optionalSemicolonToken.isEmpty() && optionalLeftParenthesis.isPresent()) break parse;

            final var result = new VarDeclaration(
                    basicType,
                    firstVarDefinition,
                    additionalVarDefinitionList,
                    optionalSemicolonToken
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
        stringBuilder
                .append(basicType.detailedRepresentation())
                .append(firstVarDefinition.detailedRepresentation());
        for (final var i : additionalVariableDefinitionList) {
            stringBuilder.append(i.commaToken().detailedRepresentation()).append(i.entity().detailedRepresentation());
        }
        optionalSemicolonToken.ifPresent(t -> stringBuilder.append(t.detailedRepresentation()));
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(basicType.representation()).append(' ')
                .append(firstVarDefinition.representation());
        for (final var i : additionalVariableDefinitionList) {
            stringBuilder
                    .append(i.commaToken().representation()).append(' ')
                    .append(i.entity().representation());
        }
        optionalSemicolonToken.ifPresent(t -> stringBuilder
                .append(t.representation())
        );
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<VarDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalSemicolonToken.isPresent()) {
            return optionalSemicolonToken.get();
        }
        if (!additionalVariableDefinitionList.isEmpty()) {
            final var lastIndex = additionalVariableDefinitionList.size() - 1;
            final var lastNonTerminator = additionalVariableDefinitionList.get(lastIndex).entity();
            return lastNonTerminator.lastTerminator();
        }
        return firstVarDefinition.lastTerminator();
    }
}
