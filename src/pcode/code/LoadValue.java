package pcode.code;

import pcode.protocols.PcodeType;

public record LoadValue(
        int level,
        int addr
) implements PcodeType {
    @Override
    public String representation() {
        return "LOD " + level + ',' + addr;
    }
}
