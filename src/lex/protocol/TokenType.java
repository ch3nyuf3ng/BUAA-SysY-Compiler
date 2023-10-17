package lex.protocol;

import foundation.Position;
import foundation.TreeNodeType;

public interface TokenType extends TreeNodeType {
    String categoryCode();
    String representation();
    Position position();
}
