package symbol;

import nonterminators.BasicType;

import java.util.List;

import static symbol.SymbolUtils.generateDimensionOffsets;

public record FunctionParameterMetadata(
        boolean isArrayPointer,
        BasicType basicType,
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
                "        basicType: " + basicType.representation() + '\n' +
                "        dimensionSizes: " + dimensionSizes + '\n' +
                "        activeRecordOffset: " + activeRecordOffset;
    }
}
