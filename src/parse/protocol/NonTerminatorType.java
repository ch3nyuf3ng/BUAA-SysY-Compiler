package parse.protocol;

import foundation.protocol.TreeNodeType;
import lex.protocol.TokenType;

public interface NonTerminatorType extends TreeNodeType {
    TokenType lastTerminator();
}