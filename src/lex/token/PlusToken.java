package lex.token;

import foundation.Position;
import lex.enums.AdditiveOperationEnum;
import lex.protocol.AdditiveTokenType;

public record PlusToken(
        Position position
) implements AdditiveTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        return "PLUS";
    }

    @Override
    public String representation() {
        return "+";
    }

    @Override
    public AdditiveOperationEnum operationType() {
        return AdditiveOperationEnum.PLUS;
    }
}
