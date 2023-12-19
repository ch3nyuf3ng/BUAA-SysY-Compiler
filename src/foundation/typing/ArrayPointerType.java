package foundation.typing;

import foundation.protocols.EvaluationType;

import java.util.List;

public record ArrayPointerType(
        EvaluationType evaluationType,
        int level,
        List<Integer> dimensionSizes
) implements EvaluationType {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArrayPointerType that = (ArrayPointerType) o;
        return level == that.level && evaluationType.equals(that.evaluationType);
    }
}
