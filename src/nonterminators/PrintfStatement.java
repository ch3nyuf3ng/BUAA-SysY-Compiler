package nonterminators;

import error.ErrorHandler;
import error.FatalErrorException;
import foundation.Pair;
import foundation.RepresentationBuilder;
import nonterminators.protocols.StatementType;
import pcode.code.WriteNumber;
import pcode.code.WriteString;
import pcode.protocols.PcodeType;
import symbol.SymbolManager;
import terminators.*;
import terminators.protocols.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PrintfStatement(
        PrintfToken printfToken,
        LeftParenthesisToken leftParenthesisToken,
        LiteralFormatStringToken literalFormatStringToken,
        List<Pair<CommaToken, Expression>> commaWithExpressionList,
        RightParenthesisToken rightParenthesisToken,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    private enum Specifier {
        STRING, INTEGER
    }

    @Override
    public String detailedRepresentation() {
        return printfToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + RepresentationBuilder.binaryOperatorExpressionDetailedRepresentation(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.detailedRepresentation()
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return printfToken.representation()
                + leftParenthesisToken.representation()
                + RepresentationBuilder.binaryOperatorExpressionRepresentation(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.representation()
                + semicolonToken.map(SemicolonToken::representation).orElse("");
    }

    @Override
    public String categoryCode() {
        return "";
    }

    @Override
    public TokenType lastTerminator() {
        return semicolonToken.isPresent() ? semicolonToken.get() : rightParenthesisToken;
    }

    @Override
    public String toString() {
        return "PrintfStatement{" +
                "printfToken=" + printfToken +
                ", leftParenthesisToken=" + leftParenthesisToken +
                ", literalFormatStringToken=" + literalFormatStringToken +
                ", commaWithExpressionList=" + commaWithExpressionList +
                ", rightParenthesisToken=" + rightParenthesisToken +
                ", semicolonToken=" + semicolonToken +
                '}';
    }

    public void generatePcode(SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler) throws FatalErrorException {
        final var parts = new ArrayList<Pair<Specifier, Object>>();
        final var originalString = literalFormatStringToken.rawRepresentation();
        final var stringBuilder = new StringBuilder();
        var expressionCount = 0;
        for (var i = 0; i < originalString.length(); i += 1) {
            final var character = originalString.charAt(i);
            final var nextCharacter = i + 1 < originalString.length() ? originalString.charAt(i + 1) : '\0';
            if (character == '\\' && nextCharacter == 'n') {
                i += 1;
                stringBuilder.append("\n");
            } else if (character == '%' && nextCharacter == 'd') {
                i += 1;
                final var stringPart = stringBuilder.toString();
                parts.add(new Pair<>(Specifier.STRING, stringPart));
                stringBuilder.delete(0, stringBuilder.length());
                final var expression = commaWithExpressionList.get(expressionCount).second();
                expressionCount += 1;
                parts.add(new Pair<>(Specifier.INTEGER, expression));
            } else {
                stringBuilder.append(character);
            }
        }
        if (!stringBuilder.isEmpty()) {
            parts.add(new Pair<>(Specifier.STRING, stringBuilder.toString()));
        }
        for (final var part : parts) {
            final var specifier = part.first();
            final var content = part.second();
            if (specifier.equals(Specifier.STRING)) {
                assert content instanceof String;
                pcodeList.add(new WriteString((String) content));
            } else if (specifier.equals(Specifier.INTEGER)) {
                assert content instanceof Expression;
                ((Expression) content).generatePcode(symbolManager, pcodeList, errorHandler);
                pcodeList.add(new WriteNumber());
            } else {
                throw new RuntimeException();
            }
        }
    }
}
