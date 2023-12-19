package nonterminators;

import error.ErrorHandler;
import nonterminators.protocols.StatementType;
import pcode.code.StackPointerMove;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.SemicolonToken;
import terminators.protocols.TokenType;

import java.util.List;
import java.util.Optional;

public record ExpressionStatement(
        Optional<Expression> expression,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    public ExpressionStatement {
        if (expression.isEmpty() && semicolonToken.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String detailedRepresentation() {
        return expression.map(Expression::detailedRepresentation).orElse("") +
                semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return expression.map(Expression::representation).orElse("") +
                semicolonToken.map(SemicolonToken::representation).orElse("");
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
            assert expression.isPresent();
            return expression.get().lastTerminator();
        }
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) {
        expression.ifPresent(value -> {
            value.generatePcode(symbolManager, pcodeList, errorHandler);
            pcodeList.add(new StackPointerMove(-1));
        });
    }
}
