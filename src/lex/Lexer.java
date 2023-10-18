package lex;

import error.IllegalCharacterError;
import foundation.ErrorHandler;
import foundation.Position;
import lex.nontoken.CommentMultiLine;
import lex.nontoken.CommentSingleLine;
import lex.nontoken.Unknown;
import lex.protocol.LexerType;
import lex.protocol.NonTokenType;
import lex.protocol.TokenType;
import lex.token.*;
import tests.foundations.Logger;

import java.util.Objects;
import java.util.Optional;

import static foundation.CharacterUtils.*;

public class Lexer implements LexerType {
    private final String sourceCode;
    private int beginningLineNumber;
    private int currentLineNumber;
    private int beginningColumnNumber;
    private int currentColumnNumber;
    private int beginningCharacterIndex;
    private int currentCharacterIndex;
    private TokenType cachedCurrentToken;

    public Lexer(String sourceCode) {
        this.sourceCode = Objects.requireNonNull(sourceCode);
        resetPosition(new Position(0, 1, 1));
    }

    @Override
    public Optional<TokenType> currentToken() {
        if (cachedCurrentToken != null) {
            return Optional.of(cachedCurrentToken); // The content in the cache is preferred.
        } else if (isAtEndOfSourceCode()) {
            cachedCurrentToken = null;
        } else if (isLetter(currentCharacter()) || isUnderline(currentCharacter())) {
            cachedCurrentToken = matchNormalAndReservedIdentifierAndUpdateCurrentPosition();
        } else if (isDigit(currentCharacter())) {
            cachedCurrentToken = matchLiteralIntegerAndUpdateCurrentPosition();
        } else if (currentCharacter() == '"') {
            cachedCurrentToken = matchLiteralFormatStringAndUpdateCurrentPosition();
        } else if (currentCharacter() == '!' && followingCharacterOfCurrentOne() == '=') {
            cachedCurrentToken = new NotEqualToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '!') {
            cachedCurrentToken = new LogicalNotToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '&' && followingCharacterOfCurrentOne() == '&') {
            cachedCurrentToken = new LogicalAndToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '|' && followingCharacterOfCurrentOne() == '|') {
            cachedCurrentToken = new LogicalOrToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '+') {
            cachedCurrentToken = new PlusToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '-') {
            cachedCurrentToken = new MinusToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '*') {
            cachedCurrentToken = new MultiplyToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '/') {
            final var token = matchCommentAndDivideOperatorAndUpdateCurrentPosition();
            if (token instanceof NonTokenType) {
                consumeToken();
                return currentToken();
            } else {
                cachedCurrentToken = token;
            }
        } else if (currentCharacter() == '%') {
            cachedCurrentToken = new ModulusToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '<' && followingCharacterOfCurrentOne() == '=') {
            cachedCurrentToken = new LessOrEqualToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '<') {
            cachedCurrentToken = new LessToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '>' && followingCharacterOfCurrentOne() == '=') {
            cachedCurrentToken = new GreaterOrEqualToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '>') {
            cachedCurrentToken = new GreaterToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '=' && followingCharacterOfCurrentOne() == '=') {
            cachedCurrentToken = new EqualToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition(2);
        } else if (currentCharacter() == '=') {
            cachedCurrentToken = new AssignToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == ';') {
            cachedCurrentToken = new SemicolonToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == ',') {
            cachedCurrentToken = new CommaToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '(') {
            cachedCurrentToken = new LeftParenthesisToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == ')') {
            cachedCurrentToken = new RightParenthesisToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '[') {
            cachedCurrentToken = new LeftBracketToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == ']') {
            cachedCurrentToken = new RightBracketToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '{') {
            cachedCurrentToken = new LeftBraceToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else if (currentCharacter() == '}') {
            cachedCurrentToken = new RightBraceToken(beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        } else {
            cachedCurrentToken = new Unknown(String.valueOf(currentCharacter()), beginningPosition());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        return Optional.ofNullable(cachedCurrentToken);
    }

    public void consumeToken() {
        if (cachedCurrentToken != null) {
            Logger.info("Consumed Token: " + cachedCurrentToken.representation());
            cachedCurrentToken = null;
        }
        skipWhitespaceAndUpdateCurrentPosition();
        syncBeginningPositionWithCurrentPosition();
    }

    @Override
    public void resetPosition(Position position) {
        if (beginningCharacterIndex != position.characterIndex()) {
            Logger.info("Lexer rolling back...");
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

    @Override
    public Position beginningPosition() {
        return new Position(beginningCharacterIndex, beginningLineNumber, beginningColumnNumber);
    }

    @Override
    public <T> Optional<T> tryMatchAndConsumeTokenOf(Class<T> targetClass) {
        if (currentToken().filter(targetClass::isInstance).isPresent()) {
            final var result = currentToken().map(targetClass::cast);
            consumeToken();
            return result;
        }
        return Optional.empty();
    }

    @Override
    public <T> boolean isMatchedTokenOf(Class<T> targetClass) {
        return currentToken().filter(targetClass::isInstance).isPresent();
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
        } else if (isLF(currentCharacter()) || isCR(currentCharacter(), followingCharacterOfCurrentOne())) {
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
            case "getint" -> new GetIntToken(beginningPosition());
            case "main" -> new MainToken(beginningPosition());
            case "printf" -> new PrintfToken(beginningPosition());
            case "break" -> new BreakToken(beginningPosition());
            case "continue" -> new ContinueToken(beginningPosition());
            case "else" -> new ElseToken(beginningPosition());
            case "for" -> new ForToken(beginningPosition());
            case "if" -> new IfToken(beginningPosition());
            case "return" -> new ReturnToken(beginningPosition());
            case "const" -> new ConstToken(beginningPosition());
            case "int" -> new IntToken(beginningPosition());
            case "void" -> new VoidToken(beginningPosition());
            default -> new IdentifierToken(identifierName, beginningPosition());
        };
    }

    private LiteralIntegerToken matchLiteralIntegerAndUpdateCurrentPosition() {
        final var unsignedIntegerBuilder = new StringBuilder();
        while (isDigit(currentCharacter())) {
            unsignedIntegerBuilder.append(currentCharacter());
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        return new LiteralIntegerToken(unsignedIntegerBuilder.toString(), beginningPosition());
    }

    private TokenType matchCommentAndDivideOperatorAndUpdateCurrentPosition() {
        final var commentBuilder = new StringBuilder();
        if (isSlash(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        if (isSlash(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            while (!isLF(currentCharacter()) && !isCR(currentCharacter(), followingCharacterOfCurrentOne()) && !isCRLF(currentCharacter(),
                    followingCharacterOfCurrentOne()
            )) {
                commentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
            }
            return new CommentSingleLine(commentBuilder.toString(), beginningPosition());
        } else if (isStar(currentCharacter())) {
            consumeCurrentCharacterAndUpdateCurrentPosition();
            while (true) {
                if (isStar(currentCharacter()) && isSlash(followingCharacterOfCurrentOne())) {
                    consumeCurrentCharacterAndUpdateCurrentPosition(2);
                    return new CommentMultiLine(commentBuilder.toString(), beginningPosition());
                } else {
                    commentBuilder.append(currentCharacter());
                    consumeCurrentCharacterAndUpdateCurrentPosition();
                }
            }
        } else {
            return new DivideToken(beginningPosition());
        }
    }

    private LiteralFormatStringToken matchLiteralFormatStringAndUpdateCurrentPosition() {
        final var literalFormatStringContentBuilder = new StringBuilder();

        if (currentCharacter() == '"') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }
        while (currentCharacter() != '"') {
            if (currentCharacter() == '\\' && followingCharacterOfCurrentOne() != 'n') {
                for (var i = 0; i < 2; ++i) {
                    literalFormatStringContentBuilder.append(currentCharacter());
                    consumeCurrentCharacterAndUpdateCurrentPosition();
                }
                ErrorHandler.outputSimpleError(new IllegalCharacterError(currentPosition()));
            } else if (!LiteralFormatStringToken.isLegalCharacter(currentCharacter())) {
                literalFormatStringContentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
                ErrorHandler.outputSimpleError(new IllegalCharacterError(currentPosition()));
            } else {
                literalFormatStringContentBuilder.append(currentCharacter());
                consumeCurrentCharacterAndUpdateCurrentPosition();
            }
        }
        if (currentCharacter() == '"') {
            consumeCurrentCharacterAndUpdateCurrentPosition();
        }

        final var literalFormatStringContent = literalFormatStringContentBuilder.toString();
        return new LiteralFormatStringToken(literalFormatStringContent, beginningPosition());
    }
}
