package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import nonterminators.protocols.NonTerminatorType;
import pcode.code.BlockEnd;
import pcode.code.BlockStart;
import pcode.code.CallFunction;
import pcode.code.Jump;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.protocols.TokenType;

import java.util.List;

public record CompileUnit(
        List<Declaration> declarationList,
        List<FuncDefinition> funcDefinitionList,
        MainFuncDefinition mainFuncDefinition
) implements NonTerminatorType {
    @Override
    public String detailedRepresentation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        funcDefinitionList.forEach(e -> stringBuilder.append(e.detailedRepresentation()));
        stringBuilder.append(mainFuncDefinition.detailedRepresentation()).append(categoryCode()).append('\n');
        return stringBuilder.toString();
    }

    @Override
    public String representation() {
        final var stringBuilder = new StringBuilder();
        declarationList.forEach(e -> stringBuilder.append(e.representation()).append('\n'));
        stringBuilder.append('\n');
        funcDefinitionList.forEach(e -> stringBuilder.append(e.representation()).append("\n\n"));
        stringBuilder.append(mainFuncDefinition.representation());
        return stringBuilder.toString();
    }

    @Override
    public String categoryCode() {
        return "<CompUnit>";
    }

    @Override
    public TokenType lastTerminator() {
        return mainFuncDefinition.lastTerminator();
    }

    public void buildSymbolTableAndGeneratePcode(
            SymbolManager symbolManager,
            List<PcodeType> pcodeList,
            ErrorHandler errorHandler
    ) throws FatalErrorException {
        pcodeList.add(new BlockStart());
        for (var declaration : declarationList) {
            declaration.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        }
        pcodeList.add(new CallFunction("#main_start", 0));
        pcodeList.add(new Jump("#main_end"));
        for (var funcDefinition : funcDefinitionList) {
            funcDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        }
        mainFuncDefinition.buildSymbolTableAndGeneratePcode(symbolManager, pcodeList, errorHandler);
        pcodeList.add(new BlockEnd(false));
    }
}
