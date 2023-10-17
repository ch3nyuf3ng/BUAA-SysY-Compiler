package lex.token;

import foundation.Position;
import lex.enums.AdditiveOperationEnum;
import lex.protocol.AdditiveTokenType;

public record MinusToken(
        Position position
) implements AdditiveTokenType {
    @Override
    public String detailedRepresentation() {
        return categoryCode() + " " + representation() + "\n";
    }

    @Override
    public String categoryCode() {
        //noinspection SpellCheckingInspection
        return "MINU";
    }

    @Override
    public String representation() {
        return "-";
    }

    @Override
    public AdditiveOperationEnum operationType() {
        return AdditiveOperationEnum.MINUS;
    }
}
