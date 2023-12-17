package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.Pair;
import foundation.RepresentationBuilder;
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
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        firstConstDefinition,
                commaWithConstDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return constToken.representation() + ' '
                + basicType.representation() + ' '
                + RepresentationBuilder.binaryOperatorExpressionRepresentation(
                        firstConstDefinition,
                commaWithConstDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "<ConstDecl>";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        if (!commaWithConstDefinitionList.isEmpty())
            return commaWithConstDefinitionList.get(commaWithConstDefinitionList.size() - 1).second()
                    .lastTerminator();
        return firstConstDefinition.lastTerminator();
    }

    @Override
    public String toString() {
        return "ConstDeclaration{" +
                "constToken=" + constToken +
                ", basicType=" + basicType +
                ", firstConstDefinition=" + firstConstDefinition +
                ", commaWithConstDefinitionList=" + commaWithConstDefinitionList +
                ", semicolonToken=" + semicolonToken +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        firstConstDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        for (var commaWithVarDefinition : commaWithConstDefinitionList) {
            final var varDefinition = commaWithVarDefinition.second();
            varDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        }
    }
}