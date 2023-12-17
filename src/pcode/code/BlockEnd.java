package pcode.code;

import pcode.protocols.PcodeType;

public record BlockEnd(boolean returnValue) implements PcodeType {
    @Override
    public String representation() {
        return "BLKE " + (returnValue ? 1 : 0);
    }
}
