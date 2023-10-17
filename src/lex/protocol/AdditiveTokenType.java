package lex.protocol;

import lex.enums.AdditiveOperationEnum;

public interface AdditiveTokenType extends TokenType {
    AdditiveOperationEnum operationType();
}
