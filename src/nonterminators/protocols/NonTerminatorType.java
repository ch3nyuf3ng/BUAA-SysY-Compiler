package nonterminators.protocols;

import foundation.protocols.TreeNodeType;
import terminators.protocols.TokenType;

public interface NonTerminatorType extends TreeNodeType {
    TokenType lastTerminator();
}