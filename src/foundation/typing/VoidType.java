package foundation.typing;

import foundation.protocols.EvaluationType;

public class VoidType implements EvaluationType {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    @Override
    public String toString() {
        return "void";
    }
}
