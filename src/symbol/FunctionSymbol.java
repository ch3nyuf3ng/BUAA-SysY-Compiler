package symbol;

import symbol.protocols.SymbolType;

public record FunctionSymbol(
        String identifier,
        FunctionMetadata metadata
) implements SymbolType {
    @Override
    public String toString() {
        return '\n' +
                "  identifier: " + identifier + '\n' +
                "  metadata: " + metadata;
    }
}
