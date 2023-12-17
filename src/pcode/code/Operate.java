package pcode.code;

import pcode.protocols.PcodeType;

public record Operate(Opcode opcode) implements PcodeType {
    public enum Opcode {
        PLUS(1),
        MINUS(2),
        MULTIPLY(3),
        DIVIDE(4),
        MODULUS(5),
        POSITIVE(6),
        NEGATIVE(7),
        LOGICAL_NOT(8),
        LOGICAL_AND(9),
        LOGICAL_OR(10),
        EQUAL(11),
        NOT_EQUAL(12),
        LESS(13),
        LESS_OR_EQUAL(14),
        GREATER(15),
        GREATER_OR_EQUAL(16);

        private final int digit;

        Opcode(int digit) {
            this.digit = digit;
        }

        public int digit() {
            return digit;
        }
    }

    @Override
    public String representation() {
        return "OPR " + opcode().digit();
    }
}
