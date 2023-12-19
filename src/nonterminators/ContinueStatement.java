package nonterminators;

import error.exceptions.BreakOrContinueMisusedException;
import foundation.Helpers;
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
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList
    ) throws BreakOrContinueMisusedException {
        if (symbolManager.loopIndex().isEmpty()) {
            throw new BreakOrContinueMisusedException(Helpers.lineNumberOf(continueToken));
        }
        final var label = "#loop" + "[" + symbolManager.loopIndex().get() + "]_iter";
        assert symbolManager.loopDepth().isPresent();
        final var recycleActiveRecordCount = symbolManager.currentDepth() - symbolManager.loopDepth().get();
        for (var i = 0; i < recycleActiveRecordCount; i += 1) {
            pcodeList.add(new BlockEnd(false));
        }
        pcodeList.add(new Jump(label));
    }
}
