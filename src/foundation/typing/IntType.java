package foundation.typing;

import foundation.protocols.EvaluationType;

public class IntType implements EvaluationType {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntType;
    }

    @Override
    public String toString() {
        return "int";
    }
}
