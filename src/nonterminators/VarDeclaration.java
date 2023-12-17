package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.Pair;
import foundation.RepresentationBuilder;
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
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        firstVarDefinition, commaWithVarDefinitionList
                  )
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("")
                + categoryCode() + '\n';
    }

    @Override
    public String representation() {
        return basicType.representation() + ' '
                + RepresentationBuilder.binaryOperatorExpressionRepresentation(
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
        if (semicolonToken.isPresent()) return semicolonToken.get();
        if (!commaWithVarDefinitionList.isEmpty()) {
            final var lastIndex = commaWithVarDefinitionList.size() - 1;
            final var lastNonTerminator = commaWithVarDefinitionList.get(lastIndex).second();
            return lastNonTerminator.lastTerminator();
        }
        return firstVarDefinition.lastTerminator();
    }

    @Override
    public String toString() {
        return "VarDeclaration{" +
                "basicType=" + basicType +
                ", firstVarDefinition=" + firstVarDefinition +
                ", commaWithVarDefinitionList=" + commaWithVarDefinitionList +
                ", semicolonToken=" + semicolonToken +
                '}';
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        firstVarDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        for (var commaWithVarDefinition : commaWithVarDefinitionList) {
            final var varDefinition = commaWithVarDefinition.second();
            varDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, basicType, errorHandler);
        }
    }
}
