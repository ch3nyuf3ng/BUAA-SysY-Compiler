package pcode.code;

import pcode.protocols.PcodeType;

public record BlockStart() implements PcodeType {
    @Override
    public String representation() {
        return "BLKS";
    }
}
