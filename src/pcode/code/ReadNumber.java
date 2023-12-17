package pcode.code;

import pcode.protocols.PcodeType;

public record ReadNumber() implements PcodeType {
    @Override
    public String representation() {
        return "RED";
    }
}
