package pcode.code;

import pcode.protocols.PcodeType;

public record ReturnFunction(
        boolean hasReturnValue
) implements PcodeType {
    @Override
    public String representation() {
        return "RET " + (hasReturnValue ? 1 : 0);
    }
}
