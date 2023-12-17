package symbol;

import symbol.protocols.SymbolType;

public record FunctionParameterSymbol(
        String identifier,
        FunctionParameterMetadata metadata
) implements SymbolType {
    @Override
    public String toString() {
        return '\n' +
                "    - identifier: " + identifier + '\n' +
                "      metadata: " + metadata;
    }
}
