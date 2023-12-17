package pcode.code;

import pcode.protocols.PcodeType;

public record MemAddZeros(
        int count
) implements PcodeType {
    @Override
    public String representation() {
        return "MAZ " + count;
    }
}
