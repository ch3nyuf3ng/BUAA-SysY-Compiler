package pcode.code;

import pcode.protocols.PcodeType;

public record CallFunction(
        String label,
        int parameterCount
) implements PcodeType {

    @Override
    public String representation() {
        return "CAL " + label;
    }
}
