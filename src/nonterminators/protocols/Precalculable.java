package nonterminators.protocols;

import error.exceptions.IdentifierUndefineException;
import symbol.SymbolManager;

public interface Precalculable {
    int calculateToInt(SymbolManager symbolManager) throws IdentifierUndefineException;
}
