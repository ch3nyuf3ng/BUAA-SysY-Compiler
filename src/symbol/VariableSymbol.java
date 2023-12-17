package symbol;

import symbol.protocols.SymbolType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record VariableSymbol(
        String identifier,
        VariableMetadata metadata,
        Optional<List<Integer>> precalculatedValue
) implements SymbolType {
    public VariableSymbol {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(metadata);
    }

    @Override
    public String toString() {
        return '\n' +
                "  identifier: " + identifier + '\n' +
                "  metadata: " + metadata + '\n' +
                "  precalculatedValue: " + precalculatedValue;
    }
}
