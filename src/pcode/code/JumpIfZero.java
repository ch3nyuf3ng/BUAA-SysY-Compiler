package pcode.code;

import pcode.protocols.PcodeType;

public record JumpIfZero(String label) implements PcodeType {
    @Override
    public String representation() {
        return "JPC " + label;
    }
}
