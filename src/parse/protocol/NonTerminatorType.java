package parse.protocol;

import foundation.TreeNodeType;
import lex.protocol.TokenType;

public interface NonTerminatorType extends TreeNodeType {
    TokenType lastTerminator();
}