package pcode.code;

import pcode.protocols.PcodeType;

public record DebugPcode(String hint) implements PcodeType {
    public static final boolean Enable = false;

    @Override
    public String representation() {
        return "DBG:" + hint;
    }
}
