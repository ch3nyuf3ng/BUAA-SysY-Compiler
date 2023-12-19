package nonterminators;

import error.ErrorHandler;
import error.exceptions.FormatStringArgCountUnmatchException;
import foundation.Helpers;
import foundation.Pair;
import foundation.ReprBuilder;
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
        Optional<RightParenthesisToken> rightParenthesisToken,
        Optional<SemicolonToken> semicolonToken
) implements StatementType {
    private enum Specifier {
        STRING, INTEGER
    }

    @Override
    public String detailedRepresentation() {
        return printfToken.detailedRepresentation()
                + leftParenthesisToken.detailedRepresentation()
                + ReprBuilder.binaryOpExpDetailedRepr(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
                + semicolonToken.map(SemicolonToken::detailedRepresentation).orElse("");
    }

    @Override
    public String representation() {
        return printfToken.representation()
                + leftParenthesisToken.representation()
                + ReprBuilder.binaryOpExRepr(
                        literalFormatStringToken, commaWithExpressionList
                  )
                + rightParenthesisToken.map(RightParenthesisToken::detailedRepresentation).orElse("")
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
        } else if (rightParenthesisToken.isPresent()) {
            return rightParenthesisToken.get();
        } else if (!commaWithExpressionList.isEmpty()) {
            final var expression = commaWithExpressionList.get(commaWithExpressionList.size() - 1).second();
            return expression.lastTerminator();
        } else {
            return literalFormatStringToken;
        }
    }

    @Override
    public String toString() {
        return representation();
    }

    public void generatePcode(
            SymbolManager symbolManager, List<PcodeType> pcodeList, ErrorHandler errorHandler
    ) throws FormatStringArgCountUnmatchException {
        final var parts = new ArrayList<Pair<Specifier, Object>>();
        final var originalString = literalFormatStringToken.rawRepresentation();
        final var stringBuilder = new StringBuilder();
        final var descriptorCount = Helpers.formatDescriptorCount(literalFormatStringToken.content());
        if (descriptorCount != commaWithExpressionList.size()) {
            throw new FormatStringArgCountUnmatchException(Helpers.lineNumberOf(printfToken));
        }
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
