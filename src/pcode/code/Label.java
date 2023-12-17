package pcode.code;

import pcode.protocols.PcodeType;

public record Label(String label) implements PcodeType {
    @Override
    public String representation() {
        return label + ':';
    }
}
