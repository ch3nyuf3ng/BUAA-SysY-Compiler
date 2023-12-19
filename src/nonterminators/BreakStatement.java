package nonterminators;

import error.exceptions.BreakOrContinueMisusedException;
import foundation.Helpers;
import nonterminators.protocols.StatementType;
import pcode.code.BlockEnd;
import pcode.code.Jump;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.BreakToken;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record BreakStatement(
        BreakToken breakToken,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    @Override
    public String detailedRepresentation() {
        return breakToken.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return breakToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        if (semicolonToken.isPresent()) {
            return semicolonToken.get();
        } else {
            return breakToken;
        }
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList
    ) throws BreakOrContinueMisusedException {
        if (symbolManager.loopIndex().isEmpty()) {
            throw new BreakOrContinueMisusedException(Helpers.lineNumberOf(breakToken));
        }
        final var label = "#loop" + "[" + symbolManager.loopIndex().get() + "]_end";
        assert symbolManager.loopDepth().isPresent();
        final var recycleActiveRecordCount = symbolManager.currentDepth() - symbolManager.loopDepth().get();
        for (var i = 0; i < recycleActiveRecordCount; i += 1) {
            pcodeList.add(new BlockEnd(false));
        }
        pcodeList.add(new Jump(label));
    }
}
