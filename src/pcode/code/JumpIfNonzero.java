package pcode.code;

import pcode.protocols.PcodeType;

public record JumpIfNonzero(String label) implements PcodeType {
    @Override
    public String representation() {
        return "JPT " + label;
    }
}
