package pcode.code;

import pcode.protocols.PcodeType;

public record StackPointerMove(int immediate) implements PcodeType {
    @Override
    public String representation() {
        return "INT " + immediate;
    }
}
