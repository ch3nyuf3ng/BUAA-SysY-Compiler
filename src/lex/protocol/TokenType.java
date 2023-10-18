package lex.protocol;

import foundation.Position;
import foundation.protocol.TreeNodeType;

public interface TokenType extends TreeNodeType {
    String categoryCode();

    String representation();

    Position position();
}
