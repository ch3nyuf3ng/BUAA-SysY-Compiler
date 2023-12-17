package pcode.code;

import pcode.protocols.PcodeType;

public record WriteNumber() implements PcodeType {
    @Override
    public String representation() {
        return "WRT";
    }
}
