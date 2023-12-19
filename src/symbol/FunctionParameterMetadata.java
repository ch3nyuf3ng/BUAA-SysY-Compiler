package symbol;

import foundation.protocols.EvaluationType;

import java.util.List;

import static symbol.SymbolUtils.generateDimensionOffsets;

public record FunctionParameterMetadata(
        boolean isArrayPointer,
        EvaluationType evaluationType,
        List<Integer> dimensionSizes,
        int activeRecordOffset
) {
    public List<Integer> dimensionOffsets() {
        return generateDimensionOffsets(dimensionSizes);
    }

    @Override
    public String toString() {
        return '\n' +
                "        isArrayPointer: " + isArrayPointer + '\n' +
                "        evaluationType: " + evaluationType + '\n' +
                "        dimensionSizes: " + dimensionSizes + '\n' +
                "        activeRecordOffset: " + activeRecordOffset;
    }
}
