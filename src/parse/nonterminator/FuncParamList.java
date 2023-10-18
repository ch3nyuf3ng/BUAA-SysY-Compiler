package parse.nonterminator;

import foundation.Pair;
import foundation.RepresentationBuilder;
import lex.protocol.LexerType;
import lex.protocol.TokenType;
import lex.token.CommaToken;
import parse.protocol.NonTerminatorType;
import tests.foundations.Logger;

import java.util.*;

public class FuncParamList implements NonTerminatorType {
    private final FuncParam firstFuncParam;
    private final List<Pair<CommaToken, FuncParam>> commaWithFuncParamList;

    public FuncParamList(FuncParam firstFuncParam, List<Pair<CommaToken, FuncParam>> commaWithFuncParamList) {
        this.firstFuncParam = Objects.requireNonNull(firstFuncParam);
        this.commaWithFuncParamList = Collections.unmodifiableList(commaWithFuncParamList);
    }

    public static Optional<FuncParamList> parse(LexerType lexer) {
        Logger.info("Matching <FuncParamList>.");
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstFuncParam = FuncParam.parse(lexer);
            if (firstFuncParam.isEmpty()) break parse;

            final var commaWithFuncParamList = new ArrayList<Pair<CommaToken, FuncParam>>();
            while (true) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var funcParam = FuncParam.parse(lexer);
                if (funcParam.isEmpty()) break parse;

                commaWithFuncParamList.add(new Pair<>(commaToken.get(), funcParam.get()));
            }

            final var result = new FuncParamList(firstFuncParam.get(), commaWithFuncParamList);
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
        final var lastNonTerminator = commaWithFuncParamList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatedConcatenatedDetailedRepresentation(
                firstFuncParam, commaWithFuncParamList
        ) + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatedConcatenatedRepresentation(
                firstFuncParam, commaWithFuncParamList
        );
    }

    @Override
    public String categoryCode() {
        return "<FuncFParams>";
    }
}
