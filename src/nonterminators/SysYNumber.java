package nonterminators;

import foundation.protocols.EvaluationType;
import foundation.typing.IntType;
import nonterminators.protocols.Precalculable;
import nonterminators.protocols.PrimaryExpressionType;
import pcode.code.LoadImmediate;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.LiteralIntegerToken;
import terminators.protocols.NumberTokenType;
import terminators.protocols.TokenType;

import java.util.List;

public record SysYNumber(
        NumberTokenType literalNumberToken
) implements PrimaryExpressionType, Precalculable {
    @Override
    public String detailedRepresentation() {
        return literalNumberToken.detailedRepresentation() + categoryCode() + "\n";
    }

    @Override
    public String representation() {
        return literalNumberToken.representation();
    }

    @Override
    public String categoryCode() {
        return "<Number>";
    }

    @Override
    public TokenType lastTerminator() {
        return literalNumberToken;
    }

    @Override
    public String toString() {
        return representation();
    }

    @Override
    public int calculateToInt(SymbolManager symbolManager) {
        return literalNumberToken().toInt();
    }

    public void generatePcode(List<PcodeType> pcodeList) {
        pcodeList.add(new LoadImmediate(literalNumberToken.toInt()));
    }

    public EvaluationType evaluationType() {
        if (literalNumberToken instanceof LiteralIntegerToken) {
            return new IntType();
        } else {
            throw new RuntimeException();
        }
    }
}
