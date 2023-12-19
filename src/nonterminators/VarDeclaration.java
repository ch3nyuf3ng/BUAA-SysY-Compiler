package nonterminators;

import error.ErrorHandler;
import error.exceptions.IdentifierRedefineException;
import error.exceptions.IdentifierUndefineException;
import foundation.Pair;
import foundation.ReprBuilder;
import nonterminators.protocols.DeclarationType;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.CommaToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record VarDeclaration(
        BasicType basicType,
        VarDefinition firstVarDefinition,
        List<Pair<CommaToken, VarDefinition>> commaWithVarDefinitionList,
        Optional<SemicolonToken> semicolonToken
) implements DeclarationType {
    @Override
    public String detailedRepresentation() {
        return basicType.detailedRepresentation()
                + ReprBuilder.binaryOpExpDetailedRepr(
                        firstVarDefinition, commaWithVarDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + ReprBuilder.binaryOpExRepr(
                        firstVarDefinition, commaWithVarDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "<VarDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) {
            return semicolonToken.get();
        }
        if (!commaWithVarDefinitionList.isEmpty()) {
            final var lastIndex = commaWithVarDefinitionList.size() - 1;
            final var lastNonTerminator = commaWithVarDefinitionList.get(lastIndex).second();
            return lastNonTerminator.lastTerminator();
        }
        return firstVarDefinition.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        try {
            firstVarDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        } catch (IdentifierRedefineException | IdentifierUndefineException e) {
            errorHandler.reportError(e);
        }
        for (var commaWithVarDefinition : commaWithVarDefinitionList) {
            final var varDefinition = commaWithVarDefinition.second();
            try {
                varDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
            } catch (IdentifierRedefineException | IdentifierUndefineException e) {
                errorHandler.reportError(e);
            }
        }
    }
}
