package pcode.code;

import pcode.protocols.PcodeType;

public record LoadAddress(int level, int addr) implements PcodeType {
    @Override
    public String representation() {
        return "LEA " + level + ',' + addr;
    }
}
