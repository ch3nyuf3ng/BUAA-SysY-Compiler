package foundation;

import nonterminators.BasicType;

public record ArrayPointer(
        BasicType basicType,
        int level
) {
    @Override
    public String toString() {
        return "ArrayPointer{" +
                "basicType=" + basicType +
                ", level=" + level +
                '}';
    }
}
