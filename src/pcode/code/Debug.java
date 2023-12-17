package pcode.code;

import pcode.protocols.PcodeType;

public record Debug(String hint) implements PcodeType {
    public static final boolean Enable = false;

    @Override
    public String representation() {
        return "DBG:" + hint;
    }
}
