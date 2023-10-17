package parse.nonterminator;

import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import parse.protocol.NonTerminatorType;
import parse.substructures.CommaWith;
import tests.foundations.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncParamList implements NonTerminatorType {
    private final FuncParam firstFuncParam;
    private final List<CommaWith<FuncParam>> commaWithFuncParamList;

    private FuncParamList(FuncParam firstFuncParam, List<CommaWith<FuncParam>> commaWithFuncParamList) {
        this.firstFuncParam = firstFuncParam;
        this.commaWithFuncParamList = commaWithFuncParamList;
    }

    public static Optional<FuncParamList> parse(LexerType lexer) {
        Logger.info("Matching <FuncParamList>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var optionalFirstFuncParam = FuncParam.parse(lexer);
            if (optionalFirstFuncParam.isEmpty()) break parse;
            final var firstFuncParam = optionalFirstFuncParam.get();

            final List<CommaWith<FuncParam>> commaWithFuncParamList = new ArrayList<>();
            while (true) {
                final var optionalCommaToken = lexer.currentToken()
                        .filter(t -> t instanceof CommaToken)
                        .map(t -> {
                            lexer.consumeToken();
                            return (CommaToken) t;
                        });
                if (optionalCommaToken.isEmpty()) break;
                final var commaToken = optionalCommaToken.get();

                final var optionalFuncParam = FuncParam.parse(lexer);
                if (optionalFuncParam.isEmpty()) break parse;
                final var funcParam = optionalFuncParam.get();

                commaWithFuncParamList.add(new CommaWith<>(commaToken, funcParam));
            }

            final var result = new FuncParamList(firstFuncParam, commaWithFuncParamList);
            Logger.info("Matched <FuncParamList>: " + result.representation());
            return Optional.of(result);
        }

        Logger.info("Failed to match <FuncParamList>.");
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @Override
    public TokenType lastTerminator() {
        if (commaWithFuncParamList.isEmpty()) {
            return firstFuncParam.lastTerminator();
        }
        final var lastIndex = commaWithFuncParamList.size() - 1;
        final var lastNonTerminator = commaWithFuncParamList.get(lastIndex).entity();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstFuncParam.detailedRepresentation());
        for (final var i : commaWithFuncParamList) {
            stringBuilder
                    .append(i.commaToken().detailedRepresentation())
                    .append(i.entity().detailedRepresentation());
        }
        stringBuilder.append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstFuncParam.representation());
        for (final var i : commaWithFuncParamList) {
            stringBuilder
                    .append(i.commaToken().representation()).append(' ')
                    .append(i.entity().representation());
        }
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<FuncFParams>";
    }
}
