package symbol;

import nonterminators.FuncType;
import symbol.protocols.Metadata;

import java.util.List;

public record FunctionMetadata(
        FuncType returnType,
        List<FunctionParameterSymbol> parameters
) implements Metadata {
    @Override
    public String toString() {
        final var string = '\n' +
                "    returnType: " + returnType.representation() + '\n' +
                "    parameters: ";
        final var stringBuilder = new StringBuilder(string);
        for (final var parameter : parameters) {
            stringBuilder.append(parameter);
        }
        return stringBuilder.toString();
    }
}
