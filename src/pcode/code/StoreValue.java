package pcode.code;

import pcode.protocols.PcodeType;

public record StoreValue(int level, int addr) implements PcodeType {
    @Override
    public String representation() {
        return "STO " + level + ',' + addr;
    }
}
