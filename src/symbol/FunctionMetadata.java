package symbol;

import foundation.protocols.EvaluationType;
import symbol.protocols.Metadata;

import java.util.List;

public record FunctionMetadata(
        EvaluationType returnType,
        List<FunctionParameterSymbol> parameters
) implements Metadata {
    @Override
    public String toString() {
        final var string = '\n' +
                "    returnType: " + returnType + '\n' +
                "    parameters: ";
        final var stringBuilder = new StringBuilder(string);
        for (final var parameter : parameters) {
            stringBuilder.append(parameter);
        }
        return stringBuilder.toString();
    }
}
