package pcode.code;

import pcode.protocols.PcodeType;

public record Jump(String label) implements PcodeType {
    @Override
    public String representation() {
        return "JMP " + label;
    }
}
