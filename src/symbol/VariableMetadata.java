package symbol;

import foundation.protocols.EvaluationType;

import java.util.List;

public record VariableMetadata(
        boolean isConstant,
        boolean isArray,
        EvaluationType evaluationType,
        List<Integer> dimensionSizes,
        int activeRecordOffset,
        int depth
) {
    public List<Integer> dimensionOffsets() {
        return SymbolUtils.generateDimensionOffsets(dimensionSizes);
    }

    public int totalSize() {
        return dimensionSizes.stream().reduce(1, (a, b) -> a * b);
    }

    @Override
    public String toString() {
        return '\n' +
                "    isConstant: " + isConstant + '\n' +
                "    isArray: " + isArray + '\n' +
                "    evaluationType: " + evaluationType + '\n' +
                "    dimensionSizes: " + dimensionSizes + '\n' +
                "    activeRecordOffset: " + activeRecordOffset + '\n' +
                "    depth: " + depth;
    }
}
