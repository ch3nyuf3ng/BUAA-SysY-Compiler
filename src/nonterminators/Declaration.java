package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.BlockItemType;
import nonterminators.protocols.DeclarationType;
import pcode.code.Debug;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record Declaration(
        DeclarationType declaration
) implements BlockItemType {
    @Override
    public TokenType lastTerminator() {
        return declaration.lastTerminator();
    }

    @Override
    public String detailedRepresentation() {
        return declaration.detailedRepresentation();
    }

    @Override
    public String representation() {
        return declaration.representation();
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "declaration=" + declaration +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        if (Debug.Enable) {
            pcodeList.add(new Debug("Declaration: " + declaration.representation()));
        }
        if (declaration instanceof VarDeclaration varDeclaration) {
            varDeclaration.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else if (declaration instanceof ConstDeclaration constDeclaration) {
            constDeclaration.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        } else {
            throw new RuntimeException();
        }
    }
}
