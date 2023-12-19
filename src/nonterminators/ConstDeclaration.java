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
import terminators.ConstToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ConstDeclaration(
        ConstToken constToken,
        BasicType basicType,
        ConstDefinition firstConstDefinition,
        List<Pair<CommaToken, ConstDefinition>> commaWithConstDefinitionList,
        Optional<SemicolonToken> semicolonToken
) implements DeclarationType {
    @Override
    public String detailedRepresentation() {
        return constToken.detailedRepresentation()
                + basicType.detailedRepresentation()
                + ReprBuilder.binaryOpExpDetailedRepr(
                        firstConstDefinition, commaWithConstDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return constToken.representation() + ' '
                + basicType.representation() + ' '
                + ReprBuilder.binaryOpExRepr(
                        firstConstDefinition, commaWithConstDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "<ConstDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) {
            return semicolonToken.get();
        }
        if (!commaWithConstDefinitionList.isEmpty()) {
            return commaWithConstDefinitionList.get(commaWithConstDefinitionList.size() - 1).second().lastTerminator();
        }
        return firstConstDefinition.lastTerminator();
    }

    @Override
    public String toString() {
        return representation();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) {
        try {
            firstConstDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        } catch (IdentifierRedefineException | IdentifierUndefineException e) {
            errorHandler.reportError(e);
        }
        for (var commaWithConstDefinition : commaWithConstDefinitionList) {
            final var constDefinition = commaWithConstDefinition.second();
            try {
                constDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
            } catch (IdentifierRedefineException | IdentifierUndefineException e) {
                errorHandler.reportError(e);
            }
        }
    }
}