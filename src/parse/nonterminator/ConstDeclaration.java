package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import lex.token.ConstToken;
import lex.token.SemicolonToken;
import parse.protocol.NonTerminatorType;
import parse.protocol.SelectionType;
import foundation.Logger;

import java.util.*;

public class ConstDeclaration implements NonTerminatorType, SelectionType {
    private final ConstToken constToken;
    private final BasicType basicType;
    private final ConstDefinition firstConstDefinition;
    private final List<Pair<CommaToken, ConstDefinition>> additionalConstantDefinitionList;
    private final Optional<SemicolonToken> semicolonToken;

    public ConstDeclaration(
            ConstToken constToken,
            BasicType basicType,
            ConstDefinition firstConstDefinition,
            List<Pair<CommaToken, ConstDefinition>> additionalConstantDefinitionList,
            Optional<SemicolonToken> semicolonToken
    ) {
        this.constToken = Objects.requireNonNull(constToken);
        this.basicType = Objects.requireNonNull(basicType);
        this.firstConstDefinition = Objects.requireNonNull(firstConstDefinition);
        this.additionalConstantDefinitionList = Collections.unmodifiableList(additionalConstantDefinitionList);
        this.semicolonToken = Objects.requireNonNull(semicolonToken);
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

            final var additionalConstDefinitionList = new ArrayList<Pair<CommaToken, ConstDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var constDefinition = ConstDefinition.parse(lexer);
                if (constDefinition.isEmpty()) break parse;

                additionalConstDefinitionList.add(new Pair<>(commaToken.get(), constDefinition.get()));
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
        return constToken.detailedRepresentation()
                + basicType.detailedRepresentation()
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        firstConstDefinition,
                        additionalConstantDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return constToken.representation() + ' '
                + basicType.representation() + ' '
                + RepresentationBuilder.binaryOperatorExpressionRepresentation(
                        firstConstDefinition,
                        additionalConstantDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "<ConstDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        if (!additionalConstantDefinitionList.isEmpty())
            return additionalConstantDefinitionList.get(additionalConstantDefinitionList.size() - 1).second()
                    .lastTerminator();
        return firstConstDefinition.lastTerminator();
    }
}