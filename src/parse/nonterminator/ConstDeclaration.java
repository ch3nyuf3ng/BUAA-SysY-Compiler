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
    private final Optional<SemicolonToken> optionalSemicolonToken;

    private ConstDeclaration(
            ConstToken constToken,
            BasicType basicType,
            ConstDefinition firstConstDefinition,
            List<CommaWith<ConstDefinition>> additionalConstantDefinitionList,
            Optional<SemicolonToken> optionalSemicolonToken
    ) {
        this.constToken = constToken;
        this.basicType = basicType;
        this.firstConstDefinition = firstConstDefinition;
        this.additionalConstantDefinitionList = additionalConstantDefinitionList;
        this.optionalSemicolonToken = optionalSemicolonToken;
    }

    public static boolean matchBeginToken(LexerType lexer) {
        return lexer.currentToken().filter(t -> t instanceof ConstToken).isPresent();
    }

    public static Optional<ConstDeclaration> parse(LexerType lexer) {
        Logger.info("Matching <ConstDeclaration>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalConstToken = lexer.currentToken()
                    .filter(t -> t instanceof ConstToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (ConstToken) t;
                    });
            if (optionalConstToken.isEmpty()) break parse;
            final var constToken = optionalConstToken.get();

            final var optionalBasicType = BasicType.parse(lexer);
            if (optionalBasicType.isEmpty()) break parse;
            final var basicType = optionalBasicType.get();

            final var optionalFirstConstDefinition = ConstDefinition.parse(lexer);
            if (optionalFirstConstDefinition.isEmpty()) break parse;
            final var firstConstDefinition = optionalFirstConstDefinition.get();

            final var additionalConstDefinitionList = new ArrayList<CommaWith<ConstDefinition>>();
            while (lexer.currentToken().isPresent()) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalConstDefinition = ConstDefinition.parse(lexer);
                if (optionalConstDefinition.isEmpty()) break parse;
                final var constDefinition = optionalConstDefinition.get();

                additionalConstDefinitionList.add(new CommaWith<>(commaToken, constDefinition));
            }

            final var optionalSemicolonToken = lexer.currentToken()
                    .filter(t -> t instanceof SemicolonToken)
                    .map(t -> {
                        lexer.consumeToken();
                        return (SemicolonToken) t;
                    });

            final var result = new ConstDeclaration(
                    constToken,
                    basicType,
                    firstConstDefinition,
                    additionalConstDefinitionList,
                    optionalSemicolonToken
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
        stringBuilder
                .append(constToken.detailedRepresentation())
                .append(basicType.detailedRepresentation())
                .append(firstConstDefinition.detailedRepresentation());
        additionalConstantDefinitionList.forEach(e -> stringBuilder
                .append(e.commaToken().detailedRepresentation())
                .append(e.entity().detailedRepresentation())
        );
        optionalSemicolonToken.ifPresent(e -> stringBuilder
                .append(e.detailedRepresentation())
        );
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder
                .append(constToken.representation()).append(' ')
                .append(basicType.representation()).append(' ')
                .append(firstConstDefinition.representation());
        additionalConstantDefinitionList.forEach(e -> stringBuilder
                .append(e.commaToken().representation()).append(' ')
                .append(e.entity().representation())
        );
        optionalSemicolonToken.ifPresent(e -> stringBuilder
                .append(e.representation())
        );
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<ConstDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (optionalSemicolonToken.isPresent()) {
            return optionalSemicolonToken.get();
        }
        if (!additionalConstantDefinitionList.isEmpty()) {
            final var lastIndex = additionalConstantDefinitionList.size() - 1;
            final var lastNonTerminator = additionalConstantDefinitionList.get(lastIndex).entity();
            return lastNonTerminator.lastTerminator();
        }
        return firstConstDefinition.lastTerminator();
    }
}