package error;

import error.errors.FuncArgNumUnmatchError;
import error.errors.IdentifierRedefinedError;
import error.errors.IdentifierUndefinedError;
import symbol.FunctionSymbol;
import symbol.SymbolManager;
import terminators.IdentifierToken;

public class ErrorChecker {
    public static void checkRedefinedIdentifier(
            SymbolManager symbolManager,
            ErrorHandler errorHandler,
            IdentifierToken identifierToken
    ) throws FatalErrorException {
        final var possibleDefinedSymbol = symbolManager.findSymbol(identifierToken.identifier(), false);
        if (possibleDefinedSymbol.isPresent()) {
            final var error = new IdentifierRedefinedError(identifierToken.endingPosition().lineNumber());
            errorHandler.reportFatalError(error);
        }
    }

    public static void checkUndefiniedIdentifier(
            SymbolManager symbolManager,
            ErrorHandler errorHandler,
            IdentifierToken identifierToken
    ) throws FatalErrorException {
        final var possibleDefinedSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleDefinedSymbol.isEmpty()) {
            final var error = new IdentifierUndefinedError(identifierToken.endingPosition().lineNumber());
            errorHandler.reportFatalError(error);
        }
    }

    public static void checkFuncArgNumUnmatchError(
            int actualArgNum,
            SymbolManager symbolManager,
            ErrorHandler errorHandler,
            IdentifierToken identifierToken
    ) {
        final var possibleSymbol = symbolManager.findSymbol(identifierToken.identifier(), true);
        if (possibleSymbol.isEmpty()) {
            return;
        }
        if (!(possibleSymbol.get() instanceof FunctionSymbol functionSymbol)) {
            return;
        }
        final var requiredArgNum = functionSymbol.metadata().parameters().size();
        if (actualArgNum != requiredArgNum) {
            final var error = new FuncArgNumUnmatchError(identifierToken.endingPosition().lineNumber());
            errorHandler.reportError(error);
        }
    }


}
