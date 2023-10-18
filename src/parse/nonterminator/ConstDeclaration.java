package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.ConstToken;
import lex.token.SemicolonToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstDeclaration implements NonTerminatorType, SelectionType {
    private final ConstToken constToken;
    private final BasicType basicType;
    private final ConstDefinition firstConstDefinition;
    private final List<CommaWith<ConstDefinition>> additionalConstantDefinitionList;
    private final Optional<SemicolonToken> semicolonToken;

    private ConstDeclaration(
            ConstToken constToken,
            BasicType basicType,
            ConstDefinition firstConstDefinition,
            List<CommaWith<ConstDefinition>> additionalConstantDefinitionList,
            Optional<SemicolonToken> semicolonToken
    ) {
        this.constToken = constToken;
        this.basicType = basicType;
        this.firstConstDefinition = firstConstDefinition;
        this.additionalConstantDefinitionList = additionalConstantDefinitionList;
        this.semicolonToken = semicolonToken;
    }

    public static boolean matchBeginToken(LexerType lexer) {
        return lexer.currentToken().filter(t -> t instanceof ConstToken).isPresent();
    }

    public static Optional<ConstDeclaration> parse(LexerType lexer) {
        Logger.info("Matching <ConstDeclaration>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var constToken = lexer.tryMatchAndConsumeTokenOf(ConstToken.class);
            if (constToken.isEmpty()) break parse;

            final var basicType = BasicType.parse(lexer);
            if (basicType.isEmpty()) break parse;

            final var firstConstDefinition = ConstDefinition.parse(lexer);
            if (firstConstDefinition.isEmpty()) break parse;

            final var additionalConstDefinitionList = new ArrayList<CommaWith<ConstDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var constDefinition = ConstDefinition.parse(lexer);
                if (constDefinition.isEmpty()) break parse;

                additionalConstDefinitionList.add(new CommaWith<>(commaToken.get(), constDefinition.get()));
            }

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new ConstDeclaration(
                    constToken.get(),
                    basicType.get(),
                    firstConstDefinition.get(),
                    additionalConstDefinitionList,
                    semicolonToken
            );
            Logger.info("Matched <ConstDeclaration>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <ConstDeclaration>");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(constToken.detailedRepresentation()).append(basicType.detailedRepresentation()).append(
                firstConstDefinition.detailedRepresentation());
        additionalConstantDefinitionList.forEach(e -> stringBuilder.append(e.commaToken().detailedRepresentation())
                .append(e.entity().detailedRepresentation()));
        semicolonToken.ifPresent(e -> stringBuilder.append(e.detailedRepresentation()));
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(constToken.representation()).append(' ').append(basicType.representation()).append(' ')
                .append(firstConstDefinition.representation());
        additionalConstantDefinitionList.forEach(e -> stringBuilder.append(e.commaToken().representation()).append(' ')
                .append(e.entity().representation()));
        semicolonToken.ifPresent(e -> stringBuilder.append(e.representation()));
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<ConstDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        if (!additionalConstantDefinitionList.isEmpty()) {
            final var lastIndex = additionalConstantDefinitionList.size() - 1;
            final var lastNonTerminator = additionalConstantDefinitionList.get(lastIndex).entity();
            return lastNonTerminator.lastTerminator();
        }
        return firstConstDefinition.lastTerminator();
    }
}