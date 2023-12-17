package pcode.code;

import pcode.protocols.PcodeType;

public record WriteString(String string) implements PcodeType {
    @Override
    public String representation() {
        return "WRTS " + '"' + string + '"';
    }
}
