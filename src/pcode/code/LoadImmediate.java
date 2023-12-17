package pcode.code;

import pcode.protocols.PcodeType;

public record LoadImmediate(Number immediate) implements PcodeType {
    @Override
    public String representation() {
        return "LIT " + immediate();
    }
}
