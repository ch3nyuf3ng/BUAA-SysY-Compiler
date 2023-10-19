package lex.protocol;

import foundation.Position;
import foundation.protocol.Representable;
import foundation.protocol.TreeNodeType;

public interface TokenType extends TreeNodeType, Representable {
    String categoryCode();

    Position position();
}
