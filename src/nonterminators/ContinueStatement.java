package nonterminators;

import nonterminators.protocols.StatementType;
import pcode.code.BlockEnd;
import pcode.code.Jump;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.ContinueToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ContinueStatement(
        ContinueToken continueToken,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return continueToken.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public String representation() {
        return continueToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) return semicolonToken.get();
        return continueToken;
    }

    @Override
    public String toString() {
        return "ContinueStatement{" +
                "continueToken=" + continueToken +
                ", semicolonToken=" + semicolonToken +
                '}';
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList) {
        final var label = "#loop" + "[" + symbolManager.loopIndex() + "]_iter";
        final var recycleActiveRecordCount = symbolManager.currentDepth() - symbolManager.loopDepth();
        for (var i = 0; i < recycleActiveRecordCount; i += 1) {
            pcodeList.add(new BlockEnd(false));
        }
        pcodeList.add(new Jump(label));
    }
}
