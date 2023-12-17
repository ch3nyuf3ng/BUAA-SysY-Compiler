package nonterminators;

import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.NonTerminatorType;
import terminators.CommaToken;
import terminators.protocols.TokenType;

import java.util.List;

public record FuncParamList(
        FuncParam firstFuncParam,
        List<Pair<CommaToken, FuncParam>> commaWithFuncParamList
) implements NonTerminatorType {
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
        return RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                firstFuncParam, commaWithFuncParamList
        ) + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(
                firstFuncParam, commaWithFuncParamList
        );
    }

    @Override
    public String categoryCode() {
        return "<FuncFParams>";
    }

    @Override
    public String toString() {
        return "FuncParamList{" +
                "firstFuncParam=" + firstFuncParam +
                ", commaWithFuncParamList=" + commaWithFuncParamList +
                '}';
    }
}
