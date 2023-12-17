package terminators.protocols;

import foundation.Position;
import foundation.protocols.Representable;
import foundation.protocols.TreeNodeType;

public interface TokenType extends TreeNodeType, Representable {
    String rawRepresentation();

    String categoryCode();

    Position beginningPosition();
    
    Position endingPosition();
}
