package foundation;

import foundation.protocol.DetailedRepresentable;
import foundation.protocol.Representable;
import lex.token.RightBracketToken;
import parse.substructures.BracketWith;

import java.util.List;

public class RepresentationBuilder {
    public static <
            Operand1 extends DetailedRepresentable,
            Operand2 extends DetailedRepresentable,
            Operator extends DetailedRepresentable>
    String binaryOperatedConcatenatedDetailedRepresentation(
            Operand1 firstOperand1, List<Pair<Operator, Operand2>> operatorWithOperandList, String categoryCode
    ) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstOperand1.detailedRepresentation()).append(categoryCode).append('\n');
        operatorWithOperandList.forEach(item -> stringBuilder
                .append(item.first().detailedRepresentation())
                .append(item.second().detailedRepresentation())
                .append(categoryCode).append('\n')
        );
        return stringBuilder.toString();
    }

    public static <
            Operand1 extends DetailedRepresentable,
            Operand2 extends DetailedRepresentable,
            Operator extends DetailedRepresentable>
    String binaryOperatedConcatenatedDetailedRepresentation(
            Operand1 firstOperand1, List<Pair<Operator, Operand2>> operatorWithOperandList
    ) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstOperand1.detailedRepresentation());
        operatorWithOperandList.forEach(item -> stringBuilder
                .append(item.first().detailedRepresentation())
                .append(item.second().detailedRepresentation())
        );
        return stringBuilder.toString();
    }

    public static <
            Operand1 extends Representable,
            Operand2 extends Representable,
            Operator extends Representable>
    String binaryOperatedConcatenatedRepresentation(
            Operand1 firstOperand1, List<Pair<Operator, Operand2>> operatorWithOperandList
    ) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(firstOperand1.representation());
        operatorWithOperandList.forEach(item -> stringBuilder
                .append(' ').append(item.first().representation())
                .append(' ').append(item.second().representation())
        );
        return stringBuilder.toString();
    }

    public static <T extends DetailedRepresentable> String bracketWithTDetailedRepresentation(
            List<BracketWith<T>> bracketWithTList
    ) {
        final var stringBuilder = new StringBuilder();
        bracketWithTList.forEach(t -> stringBuilder
                .append(t.leftBracketToken().detailedRepresentation())
                .append(t.entity().detailedRepresentation())
                .append(t.rightBracketToken().map(RightBracketToken::detailedRepresentation).orElse(""))
        );
        return stringBuilder.toString();
    }

    public static <T extends Representable> String bracketWithTRepresentation(
            List<BracketWith<T>> bracketWithTList
    ) {
        final var stringBuilder = new StringBuilder();
        bracketWithTList.forEach(t -> stringBuilder
                .append(t.leftBracketToken().representation())
                .append(t.entity().representation())
                .append(t.rightBracketToken().map(RightBracketToken::representation).orElse(""))
        );
        return stringBuilder.toString();
    }
}
