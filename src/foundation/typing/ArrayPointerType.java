package foundation.typing;

import foundation.protocols.EvaluationType;

import java.util.Objects;

public record ArrayPointerType(
        EvaluationType evaluationType,
        int level
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
        return level == that.level && Objects.equals(evaluationType, that.evaluationType);
    }
}
