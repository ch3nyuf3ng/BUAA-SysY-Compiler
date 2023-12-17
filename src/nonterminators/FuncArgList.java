package nonterminators;

import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.NonTerminatorType;
import terminators.CommaToken;
import terminators.protocols.TokenType;

import java.util.List;

public record FuncArgList(
        Expression firstExpression,
        List<Pair<CommaToken, Expression>> commaWithExpressionList
) implements NonTerminatorType {
    @Override
    public TokenType lastTerminator() {
        if (commaWithExpressionList.isEmpty()) return firstExpression.lastTerminator();
        final var lastIndex = commaWithExpressionList.size() - 1;
        final var lastNonTerminator = commaWithExpressionList.get(lastIndex).second();
        return lastNonTerminator.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                firstExpression, commaWithExpressionList
        ) + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return RepresentationBuilder.binaryOperatorExpressionRepresentation(
                firstExpression, commaWithExpressionList
        );
    }

    @Override
    public String categoryCode() {
        return "<FuncRParams>";
    }

    @Override
    public String toString() {
        return "FuncArgList{" +
                "firstExpression=" + firstExpression +
                ", commaWithExpressionList=" + commaWithExpressionList +
                '}';
    }

    public int argCount() {
        return 1 + commaWithExpressionList.size();
    }
}
