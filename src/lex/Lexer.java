package lex;

import error.ErrorHandler;
import error.errors.IllegalCharacterError;
import foundation.Logger;
import foundation.Position;
import foundation.Helpers;
import terminators.*;
import terminators.nontokens.CommentMultiLine;
import terminators.nontokens.CommentSingleLine;
import terminators.nontokens.UnknownToken;
import terminators.protocols.NonTokenType;
import terminators.protocols.TokenType;

import java.util.Objects;
import java.util.Optional;

import static foundation.CharacterUtils.*;

public class Lexer {
    private final ErrorHandler errorHandler;
    private final String sourceCode;
    private int beginningLineNumber;
    private int currentLineNumber;
    private int beginningColumnNumber;
    private int currentColumnNumber;
    private int beginningCharacterIndex;
    private int currentCharacterIndex;
    private TokenType cachedCurrentToken;

    public Lexer(ErrorHandler errorHandler, String sourceCode) {
        this.errorHandler = errorHandler;
        this.sourceCode = Objects.requireNonNull(sourceCode);
        resetPosition(new Position(0, 1, 1));
    }

    public Optional<TokenType> currentToken() {
        if (cachedCurrentToken != null) {
            return Optional.of(cachedCurrentToken);
        }

        if (isAtEndOfSourceCode()) {
            cachedCurrentToken = null;
        } else if (isLetter(currentCharacter()) || isUnderline(currentCharacter())) {
            cachedCurrentToken = matchNormalAndReservedIdentifierAndUpdateCurrentPosition();
        } else if (isDigit(currentCharacter())) {
            cachedCurrentToken = matchLiteralIntegerAndUpdateCurrentPosition();
        } else if (currentCharacter() == '"') {
            cachedCurrentToken = matchLiteralFormatStringAndUpdateCurrentPosition();
        } else if (currentCharacter() == '!' && followingCharacterOfCurrentOne() == '=') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new NotEqualToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '!') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new LogicalNotToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '&' && followingCharacterOfCurrentOne() == '&') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new LogicalAndToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '|' && followingCharacterOfCurrentOne() == '|') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new LogicalOrToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '+') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new PlusToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '-') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new MinusToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '*') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new MultiplyToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '/') {
            final var token = matchCommentAndDivideOperatorAndUpdateCurrentPosition();
            if (token instanceof NonTokenType) {
                consumeToken();
                return currentToken();
            } else {
                cachedCurrentToken = token;
            }
        } else if (currentCharacter() == '%') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new ModulusToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '<' && followingCharacterOfCurrentOne() == '=') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new LessOrEqualToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '<') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new LessToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '>' && followingCharacterOfCurrentOne() == '=') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new GreaterOrEqualToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '>') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new GreaterToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '=' && followingCharacterOfCurrentOne() == '=') {
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
            cachedCurrentToken = new EqualToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '=') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new AssignToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == ';') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new SemicolonToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == ',') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new CommaToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '(') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new LeftParenthesisToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == ')') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new RightParenthesisToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '[') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new LeftBracketToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == ']') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new RightBracketToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '{') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new LeftBraceToken(beginningPosition(), currentPosition());
        } else if (currentCharacter() == '}') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new RightBraceToken(beginningPosition(), currentPosition());
        } else {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            cachedCurrentToken = new UnknownToken(
                    String.valueOf(currentCharacter()),
                    beginningPosition(),
                    currentPosition()
            );
        }

        return Optional.ofNullable(cachedCurrentToken);
    }

    public void consumeToken() {
        if (cachedCurrentToken != null) {
            if (Logger.LogEnabled) {
                Logger.debug("Consumed Token: " + cachedCurrentToken.representation(), Logger.Category.LEXER);
            }
            cachedCurrentToken = null;
        }
        skipWhitespaceAndUpdateCurrentPosition();
        syncBeginningPositionWithCurrentPosition();
    }

    public void resetPosition(Position position) {
        if (beginningCharacterIndex != position.characterIndex()) {
            if (Logger.LogEnabled) {
                Logger.debug("Lexer rolling back...", Logger.Category.LEXER);
            }
        }
        beginningLineNumber = currentLineNumber = position.lineNumber();
        beginningColumnNumber = currentColumnNumber = position.columnNumber();
        beginningCharacterIndex = currentCharacterIndex = position.characterIndex();
        cachedCurrentToken = null;
        skipWhitespaceAndUpdateCurrentPosition();
        syncBeginningPositionWithCurrentPosition();
    }

    private Position currentPosition() {
        return new Position(currentCharacterIndex, currentLineNumber, currentColumnNumber);
    }

    public Position beginningPosition() {
        return new Position(beginningCharacterIndex, beginningLineNumber, beginningColumnNumber);
    }

    public <T> Optional<T> tryMatchAndConsumeTokenOf(Class<T> targetTokenClass) {
        if (currentToken().filter(targetTokenClass::isInstance).isPresent()) {
            final var result = currentToken().map(targetTokenClass::cast);
            consumeToken();
            return result;
        }
        return Optional.empty();
    }

    public <T> boolean isMatchedTokenOf(Class<T> targetTokenClass) {
        return currentToken().filter(targetTokenClass::isInstance).isPresent();
    }

    public String getLineAt(Position position) {
        return Helpers.readLine(sourceCode, position);
    }

    private boolean isAtEndOfSourceCode() {
        return currentCharacterIndex >= sourceCode.length();
    }

    private boolean characterIndexInRange(int index) {
        return 0 <= index && index < sourceCode.length();
    }

    private char currentCharacter() {
        if (characterIndexInRange(currentCharacterIndex)) {
            return sourceCode.charAt(currentCharacterIndex);
        } else {
            return '\0';
        }
    }

    private char followingCharacterOfCurrentOne() {
        if (characterIndexInRange(currentCharacterIndex + 1)) {
            return sourceCode.charAt(currentCharacterIndex + 1);
        } else {
            return '\0';
        }
    }

    private void consumeCurrentCharacterAndUpdateCurrentPosition() {
        if (isCRLF(currentCharacter(), followingCharacterOfCurrentOne())) {
            currentColumnNumber = 1;
            currentLineNumber += 1;
            currentCharacterIndex += 2;
        } else if (isLF(currentCharacter()) || isCR(currentCharacter())) {
            currentColumnNumber = 1;
            currentLineNumber += 1;
            currentCharacterIndex += 1;
        } else {
            currentColumnNumber += 1;
            currentCharacterIndex += 1;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void consumeCurrentCharacterAndUpdateCurrentPosition(int count) {
        for (var i = 0; i < count; i += 1) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
    }

    private void syncBeginningPositionWithCurrentPosition() {
        beginningLineNumber = currentLineNumber;
        beginningColumnNumber = currentColumnNumber;
        beginningCharacterIndex = currentCharacterIndex;
    }

    private void skipWhitespaceAndUpdateCurrentPosition() {
        while (isGeneralWhitespace(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
    }

    private TokenType matchNormalAndReservedIdentifierAndUpdateCurrentPosition() {
        final var identifierNameBuilder = new StringBuilder();
        if (isLetter(currentCharacter()) || isUnderline(currentCharacter())) {
            identifierNameBuilder.append(currentCharacter());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        while (isLetter(currentCharacter()) || isUnderline(currentCharacter()) || isDigit(currentCharacter())) {
            identifierNameBuilder.append(currentCharacter());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        final var identifierName = identifierNameBuilder.toString();
        return switch (identifierName) {
            case "getint" -> new GetIntToken(beginningPosition(), currentPosition());
            case "main" -> new MainToken(beginningPosition(), currentPosition());
            case "printf" -> new PrintfToken(beginningPosition(), currentPosition());
            case "break" -> new BreakToken(beginningPosition(), currentPosition());
            case "continue" -> new ContinueToken(beginningPosition(), currentPosition());
            case "else" -> new ElseToken(beginningPosition(), currentPosition());
            case "for" -> new ForToken(beginningPosition(), currentPosition());
            case "if" -> new IfToken(beginningPosition(), currentPosition());
            case "return" -> new ReturnToken(beginningPosition(), currentPosition());
            case "const" -> new ConstToken(beginningPosition(), currentPosition());
            case "int" -> new IntToken(beginningPosition(), currentPosition());
            case "void" -> new VoidToken(beginningPosition(), currentPosition());
            default -> new IdentifierToken(identifierName, beginningPosition(), currentPosition());
        };
    }

    private LiteralIntegerToken matchLiteralIntegerAndUpdateCurrentPosition() {
        final var unsignedIntegerBuilder = new StringBuilder();
        while (isDigit(currentCharacter())) {
            unsignedIntegerBuilder.append(currentCharacter());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        return new LiteralIntegerToken(unsignedIntegerBuilder.toString(), beginningPosition(), currentPosition());
    }

    private TokenType matchCommentAndDivideOperatorAndUpdateCurrentPosition() {
        final var commentBuilder = new StringBuilder();
        if (isSlash(currentCharacter())) consumeCurrentCharacterAndUpdateCurrentPosition();
        if (isSlash(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            while (!isCRLF(currentCharacter(), followingCharacterOfCurrentOne())
                    && !isCR(currentCharacter()) && !isLF(currentCharacter())
            ) {
                commentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
            }
            return new CommentSingleLine(commentBuilder.toString(), beginningPosition(), currentPosition());
        } else if (isStar(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            while (true) {
                if (isStar(currentCharacter()) && isSlash(followingCharacterOfCurrentOne())) {
                    consumeCurrentCharacterAndUpdateCurrentPosition(2);
                    return new CommentMultiLine(commentBuilder.toString(), beginningPosition(), currentPosition());
                } else {
                    commentBuilder.append(currentCharacter());
                    consumeCurrentCharacterAndUpdateCurrentPosition();
                }
            }
        } else {
            return new DivideToken(beginningPosition(), currentPosition());
        }
    }

    private LiteralFormatStringToken matchLiteralFormatStringAndUpdateCurrentPosition() {
        final var literalFormatStringContentBuilder = new StringBuilder();

        if (currentCharacter() == '"') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        while (currentCharacter() != '"') {
            if (currentCharacter() == '\\' && followingCharacterOfCurrentOne() != 'n') {
                final var illegalCharacterBeginningPosition = currentPosition();
                literalFormatStringContentBuilder.append(currentCharacter()).append(followingCharacterOfCurrentOne());
                if (followingCharacterOfCurrentOne() != '"') {
                    consumeCurrentCharacterAndUpdateCurrentPosition(2);
                } else {
                    consumeCurrentCharacterAndUpdateCurrentPosition();
                }
                final var error = new IllegalCharacterError(illegalCharacterBeginningPosition.lineNumber());
                errorHandler.reportError(error);
            } else if (currentCharacter() == '%' && followingCharacterOfCurrentOne() == 'd') {
                literalFormatStringContentBuilder.append(currentCharacter()).append(followingCharacterOfCurrentOne());
                consumeCurrentCharacterAndUpdateCurrentPosition(2);
            } else if (!LiteralFormatStringToken.isLegalCharacter(currentCharacter())) {
                final var illegalCharacterBeginningPosition = currentPosition();
                literalFormatStringContentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
                final var error = new IllegalCharacterError(illegalCharacterBeginningPosition.lineNumber());
                errorHandler.reportError(error);
            } else {
                literalFormatStringContentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
            }
        }
        if (currentCharacter() == '"') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }

        final var literalFormatStringContent = literalFormatStringContentBuilder.toString();
        return new LiteralFormatStringToken(literalFormatStringContent, beginningPosition(), currentPosition());
    }

    @Override
    public String toString() {
        return "Lexer{" +
                "errorHandler=" + errorHandler +
                ", sourceCode='" + sourceCode + '\'' +
                ", beginningLineNumber=" + beginningLineNumber +
                ", currentLineNumber=" + currentLineNumber +
                ", beginningColumnNumber=" + beginningColumnNumber +
                ", currentColumnNumber=" + currentColumnNumber +
                ", beginningCharacterIndex=" + beginningCharacterIndex +
                ", currentCharacterIndex=" + currentCharacterIndex +
                ", cachedCurrentToken=" + cachedCurrentToken +
                '}';
    }
}
