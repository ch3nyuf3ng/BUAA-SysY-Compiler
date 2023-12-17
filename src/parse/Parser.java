package parse;

import error.ErrorHandler;
import error.errors.MissingRightParenthesisError;
import error.errors.MissingSemicolonError;
import foundation.BracketWith;
import foundation.Logger;
import foundation.Pair;
import foundation.Position;
import lex.Lexer;
import nonterminators.*;
import terminators.*;
import terminators.protocols.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Parser {
    private final ErrorHandler errorHandler;
    private final Lexer lexer;

    public Parser(ErrorHandler errorHandler, Lexer lexer) {
        this.errorHandler = errorHandler;
        this.lexer = lexer;
    }

    public Optional<CompileUnit> parse() {
        lexer.resetPosition(new Position(0, 1, 1));
        return parseCompileUnit();
    }

    private boolean isMatchedBeginningTokenOfPrimaryExpression() {
        return lexer.isMatchedTokenOf(LeftParenthesisToken.class)
                || lexer.isMatchedTokenOf(IdentifierToken.class)
                || lexer.isMatchedTokenOf(LiteralIntegerToken.class);
    }

    private boolean isMatchedBeginningTokenOfPrintfStatement() {
        return lexer.isMatchedTokenOf(PrintfToken.class);
    }

    private boolean isMatchedBeginningTokenOfReturnStatement() {
        return lexer.isMatchedTokenOf(ReturnToken.class);
    }

    private Optional<ArrayConstInitValue> parseArrayConstInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ArrayConstInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final Optional<ConstInitValue> optionalFirstInitValue = parseConstInitValue();

            final var otherInitValueList = new ArrayList<Pair<CommaToken, ConstInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var anotherInitValue = parseConstInitValue();
                if (anotherInitValue.isEmpty()) break parse;

                otherInitValueList.add(new Pair<>(commaToken.get(), anotherInitValue.get()));
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new ArrayConstInitValue(
                    leftBraceToken.get(), optionalFirstInitValue, otherInitValueList, rightBraceToken.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ArrayConstInitValue>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ArrayConstInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<PrimaryExpression> parsePrimaryExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <PrimaryExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(LeftParenthesisToken.class)) {
            final var parenthesisedPrimeExpression = parseParenthesisedPrimeExpression();
            if (parenthesisedPrimeExpression.isPresent()) {
                final var result = new PrimaryExpression(parenthesisedPrimeExpression.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <PrimaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (lexer.isMatchedTokenOf(IdentifierToken.class)) {
            final var leftValue = parseLeftValue();
            if (leftValue.isPresent()) {
                final var result = new PrimaryExpression(leftValue.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <PrimaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (lexer.isMatchedTokenOf(LiteralIntegerToken.class)) {
            final var number = parseNumber();
            if (number.isPresent()) {
                final var result = new PrimaryExpression(number.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <PrimaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <PrimaryExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<PrintfStatement> parsePrintfStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <PrintfStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var printfToken = lexer.tryMatchAndConsumeTokenOf(PrintfToken.class);
            if (printfToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var literalFormatStringToken = lexer.tryMatchAndConsumeTokenOf(LiteralFormatStringToken.class);
            if (literalFormatStringToken.isEmpty()) break parse;

            final var commaWithExpressionList = new ArrayList<Pair<CommaToken, Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var expression = parseExpression();
                if (expression.isEmpty()) break;

                commaWithExpressionList.add(new Pair<>(commaToken.get(), expression.get()));
            }

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new PrintfStatement(
                    printfToken.get(), leftParenthesisToken.get(),
                    literalFormatStringToken.get(), commaWithExpressionList,
                    rightParenthesisToken.get(), semicolonToken
            );
            if (semicolonToken.isEmpty()) {
                final var lastTerminator = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastTerminator.endingPosition()),
                        lastTerminator.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <PrintfStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <PrintfStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<RelationalExpression> parseRelationalExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <RelationalExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstAdditiveExpression = parseAdditiveExpression();
            if (firstAdditiveExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<RelaitionalOperatorTokenType, AdditiveExpression>>();
            while (lexer.isMatchedTokenOf(RelaitionalOperatorTokenType.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(RelaitionalOperatorTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalAdditiveExpression = parseAdditiveExpression();
                if (additionalAdditiveExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalAdditiveExpression.get()));
            }

            final var result = new RelationalExpression(firstAdditiveExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <RelationalExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <RelationalExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ReturnStatement> parseReturnStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ReturnStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var returnToken = lexer.tryMatchAndConsumeTokenOf(ReturnToken.class);
            if (returnToken.isEmpty()) break parse;

            final var expression = parseExpression();

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new ReturnStatement(returnToken.get(), expression, semicolonToken);
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ReturnStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ReturnStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<AdditiveExpression> parseAdditiveExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <AdditiveExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstExpression = parseMultiplicativeExpression();
            if (firstExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<AdditiveTokenType, MultiplicativeExpression>>();
            while (true) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(AdditiveTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalMultiplicativeExpression = parseMultiplicativeExpression();
                if (additionalMultiplicativeExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalMultiplicativeExpression.get()));
            }

            final var result = new AdditiveExpression(firstExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <AdditiveExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <AdditiveExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ArrayVarInitValue> parseArrayVarInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ArrayVarInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final Optional<VarInitValue> optionalFirstInitValue = parseVarInitValue();

            final var otherInitValueList = new ArrayList<Pair<CommaToken, VarInitValue>>();
            while (optionalFirstInitValue.isPresent()) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var anotherInitValue = parseVarInitValue();
                if (anotherInitValue.isEmpty()) break parse;

                otherInitValueList.add(new Pair<>(commaToken.get(), anotherInitValue.get()));
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new ArrayVarInitValue(
                    leftBraceToken.get(), optionalFirstInitValue,
                    otherInitValueList, rightBraceToken.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ArrayVarInitValue>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ArrayVarInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @SuppressWarnings("DuplicatedCode")
    private Optional<AssignmentStatement> parseAssignmentStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <AssignmentStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = parseLeftValue();
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var expression = parseExpression();
            if (expression.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new AssignmentStatement(
                    leftValue.get(), assignToken.get(), expression.get(), semicolonToken
            );
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <AssignmentStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <AssignmentStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<BasicType> parseBasicType() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <BasicType>", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var token = lexer.tryMatchAndConsumeTokenOf(BasicTypeTokenType.class);
            if (token.isEmpty()) break parse;

            final var result = new BasicType(token.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <BasicType>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <BasicType>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfBlock() {
        return lexer.isMatchedTokenOf(LeftBraceToken.class);
    }

    private Optional<Block> parseBlock() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Block>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftBraceToken = lexer.tryMatchAndConsumeTokenOf(LeftBraceToken.class);
            if (leftBraceToken.isEmpty()) break parse;

            final List<BlockItem> blockItemList = new ArrayList<>();
            while (isMatchedBeginningTokensOfBlockItem()) {
                final var blockItem = parseBlockItem();
                if (blockItem.isEmpty()) break;
                blockItemList.add(blockItem.get());
            }

            final var rightBraceToken = lexer.tryMatchAndConsumeTokenOf(RightBraceToken.class);
            if (rightBraceToken.isEmpty()) break parse;

            final var result = new Block(leftBraceToken.get(), blockItemList, rightBraceToken.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <Block>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Block>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfBlockItem() {
        return isMatchedBeginningTokensOfDeclaration() || isMatchedBeginningTokensOfStatement();
    }

    private Optional<BlockItem> parseBlockItem() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <BlockItem>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (isMatchedBeginningTokensOfDeclaration()) {
            final var declaration = parseDeclaration();
            if (declaration.isPresent()) {
                final var result = new BlockItem(declaration.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <BlockItem>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokensOfStatement()) {
            final var statement = parseStatement();
            if (statement.isPresent()) {
                final var result = new BlockItem(statement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <BlockItem>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <BlockItem>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfBreakStatement() {
        return lexer.isMatchedTokenOf(BreakToken.class);
    }

    private Optional<BreakStatement> parseBreakStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <BreakStatement>.", Logger.Category.PARSER);
        }
        final var beginnningPosition = lexer.beginningPosition();

        parse:
        {
            final var breakToken = lexer.tryMatchAndConsumeTokenOf(BreakToken.class);
            if (breakToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new BreakStatement(breakToken.get(), semicolonToken);
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <BreakStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <BreakStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginnningPosition);
        return Optional.empty();
    }

    private Optional<CompileUnit> parseCompileUnit() {
        final var beginningPosition = lexer.beginningPosition();
        if (Logger.LogEnabled) {
            Logger.debug("Matching <CompileUnit>", Logger.Category.PARSER);
        }
        parse:
        {
            final List<Declaration> declarationList = new ArrayList<>();
            while (isMatchedBeginningTokensOfDeclaration()) {
                final var declaration = parseDeclaration();
                if (declaration.isEmpty()) break;
                declarationList.add(declaration.get());
            }

            final List<FuncDefinition> funcDefinitionList = new ArrayList<>();
            while (isMatchedBeginningTokensOfFuncDefinition()) {
                final var funcDefinition = parseFuncDefinition();
                if (funcDefinition.isEmpty()) break;
                funcDefinitionList.add(funcDefinition.get());
            }

            final var mainFuncDefinition = parseMainFuncDefinition();
            if (mainFuncDefinition.isEmpty()) break parse;

            final var result = new CompileUnit(declarationList, funcDefinitionList, mainFuncDefinition.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <CompileUnit>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <CompileUnit>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<Condition> parseCondition() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Condition>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var logicalOrExpression = parseLogicalOrExpression();
            if (logicalOrExpression.isEmpty()) break parse;

            final var result = new Condition(logicalOrExpression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <Condition>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Condition>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfConstDeclaration() {
        return lexer.currentToken().filter(t -> t instanceof ConstToken).isPresent();
    }

    private Optional<ConstDeclaration> parseConstDeclaration() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ConstDeclaration>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var constToken = lexer.tryMatchAndConsumeTokenOf(ConstToken.class);
            if (constToken.isEmpty()) break parse;

            final var basicType = parseBasicType();
            if (basicType.isEmpty()) break parse;

            final var firstConstDefinition = parseConstDefinition();
            if (firstConstDefinition.isEmpty()) break parse;

            final var additionalConstDefinitionList = new ArrayList<Pair<CommaToken, ConstDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var constDefinition = parseConstDefinition();
                if (constDefinition.isEmpty()) break parse;

                additionalConstDefinitionList.add(new Pair<>(commaToken.get(), constDefinition.get()));
            }

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new ConstDeclaration(
                    constToken.get(),
                    basicType.get(),
                    firstConstDefinition.get(),
                    Collections.unmodifiableList(additionalConstDefinitionList),
                    semicolonToken
            );
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ConstDeclaration>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ConstDeclaration>", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ConstDefinition> parseConstDefinition() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ConstDefinition>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final List<BracketWith<ConstExpression>> bracketWithConstExpressionList = new ArrayList<>();
            while (lexer.currentToken().isPresent()) {
                final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (leftBracketToken.isEmpty()) break;

                final var constExpression = parseConstExpression();
                if (constExpression.isEmpty()) break parse;

                final var rightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);

                bracketWithConstExpressionList.add(new BracketWith<>(
                        leftBracketToken.get(),
                        constExpression.get(),
                        rightBracketToken
                ));
            }

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var constInitValue = parseConstInitValue();
            if (constInitValue.isEmpty()) break parse;

            final var result = new ConstDefinition(
                    identifierToken.get(),
                    bracketWithConstExpressionList,
                    assignToken.get(),
                    constInitValue.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ConstDefinition>." + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ConstDefinition>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ConstExpression> parseConstExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ConstExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var additiveExpression = parseAdditiveExpression();
            if (additiveExpression.isEmpty()) break parse;

            final var result = new ConstExpression(additiveExpression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ConstExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ConstExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ConstInitValue> parseConstInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ConstInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(LeftBraceToken.class)) {
            final var arrayConstInitValue = parseArrayConstInitValue();
            if (arrayConstInitValue.isPresent()) {
                final var result = new ConstInitValue(arrayConstInitValue.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <ConstInitValue>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        } else {
            final var scalarConstInitValue = parseScalarConstInitValue();
            if (scalarConstInitValue.isPresent()) {
                final var result = new ConstInitValue(scalarConstInitValue.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <ConstInitValue>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ConstInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfContinueStatement() {
        return lexer.isMatchedTokenOf(ContinueToken.class);
    }

    private Optional<ContinueStatement> parseContinueStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ContinueStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var continueToken = lexer.tryMatchAndConsumeTokenOf(ContinueToken.class);
            if (continueToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new ContinueStatement(continueToken.get(), semicolonToken);
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ContinueStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ContinueStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfDeclaration() {
        return isMatchedBeginningTokensOfVarDeclaration() || isMatchedBeginningTokensOfConstDeclaration();
    }

    private Optional<Declaration> parseDeclaration() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Declaration>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (isMatchedBeginningTokensOfConstDeclaration()) {
            final var constDeclaration = parseConstDeclaration();
            if (constDeclaration.isPresent()) {
                final var result = new Declaration(constDeclaration.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Declaration>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokensOfVarDeclaration()) {
            final var varDeclaration = parseVarDeclaration();
            if (varDeclaration.isPresent()) {
                final var result = new Declaration(varDeclaration.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Declaration>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Declaration>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<EqualityExpression> parseEqualityExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <EqualityExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstExpression = parseRelationalExpression();
            if (firstExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<EqualityTokenType, RelationalExpression>>();
            while (lexer.isMatchedTokenOf(EqualityTokenType.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(EqualityTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalRelationalExpression = parseRelationalExpression();
                if (additionalRelationalExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalRelationalExpression.get()));
            }

            final var result = new EqualityExpression(firstExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <EqualityExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <EqualityExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<Expression> parseExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Expression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var additiveExpression = parseAdditiveExpression();
            if (additiveExpression.isEmpty()) break parse;

            final var result = new Expression(additiveExpression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <Expression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Expression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfExpressionStatementToken() {
        return lexer.isMatchedTokenOf(LeftParenthesisToken.class)
                || lexer.isMatchedTokenOf(LiteralIntegerToken.class)
                || lexer.isMatchedTokenOf(PlusToken.class)
                || lexer.isMatchedTokenOf(MinusToken.class)
                || lexer.isMatchedTokenOf(LogicalNotToken.class)
                || lexer.isMatchedTokenOf(SemicolonToken.class)
                || lexer.isMatchedTokenOf(IdentifierToken.class);
    }

    private Optional<ExpressionStatement> parseExpressionStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ExpressionStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = parseExpression();
            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            if (semicolonToken.isEmpty()) break parse;

            final var result = new ExpressionStatement(expression, semicolonToken.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ExpressionStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ExpressionStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    @SuppressWarnings("DuplicatedCode")
    private Optional<ForStatement> parseForStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ForStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = parseLeftValue();
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var expression = parseExpression();
            if (expression.isEmpty()) break parse;

            final var result = new ForStatement(leftValue.get(), assignToken.get(), expression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ForStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ForStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfForLoopStatement() {
        return lexer.isMatchedTokenOf(ForToken.class);
    }

    private Optional<ForLoopStatement> parseForLoopStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ForStatementSelection>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var forToken = lexer.tryMatchAndConsumeTokenOf(ForToken.class);
            if (forToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var initStatement = parseForStatement();

            final var semicolonToken1 = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken1.isEmpty()) break parse;

            final var condition = parseCondition();

            final var semicolonToken2 = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);
            if (semicolonToken2.isEmpty()) break parse;

            final var iterateStatement = parseForStatement();

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var statement = parseStatement();
            if (statement.isEmpty()) break parse;

            final var result = new ForLoopStatement(
                    forToken.get(), leftParenthesisToken.get(),
                    initStatement, semicolonToken1.get(),
                    condition, semicolonToken2.get(),
                    iterateStatement, rightParenthesisToken.get(),
                    statement.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ForStatementSelection>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ForStatementSelection>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<FuncArgList> parseFuncArgList() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncArgList>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = parseExpression();
            if (expression.isEmpty()) break parse;

            final var commaWithExpressionList = new ArrayList<Pair<CommaToken, Expression>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var additionalExpression = parseExpression();
                if (additionalExpression.isEmpty()) break parse;

                commaWithExpressionList.add(new Pair<>(commaToken.get(), additionalExpression.get()));
            }

            final var result = new FuncArgList(expression.get(), commaWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncArgList>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncArgList>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfFuncDefinition() {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.tryMatchAndConsumeTokenOf(FuncTypeTokenType.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class))
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class)).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    private Optional<FuncDefinition> parseFuncDefinition() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncDefinition>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var funcType = parseFuncType();
            if (funcType.isEmpty()) break parse;

            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var funcParaList = parseFuncParamList();

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);

            final var block = parseBlock();
            if (block.isEmpty()) break parse;

            final var result = new FuncDefinition(
                    funcType.get(), identifierToken.get(), leftParenthesisToken.get(),
                    funcParaList, rightParenthesisToken, block.get()
            );
            if (rightParenthesisToken.isEmpty()) {
                final var error = new MissingRightParenthesisError(result.lastTerminator().endingPosition().lineNumber());
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncDefinition>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncDefinition>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfFuncInvocation() {
        final var beginningPosition = lexer.beginningPosition();
        final var result = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class)).isPresent();
        lexer.resetPosition(beginningPosition);
        return result;
    }

    private Optional<FuncInvocation> parseFuncInvocation() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncInvocation>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var funcArgList = parseFuncArgList();

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);

            final var result = new FuncInvocation(
                    identifierToken.get(),
                    leftParenthesisToken.get(),
                    funcArgList,
                    rightParenthesisToken
            );
            if (rightParenthesisToken.isEmpty()) {
                final var error = new MissingRightParenthesisError(result.lastTerminator().endingPosition().lineNumber());
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncInvocation>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncInvocation>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<FuncParam> parseFuncParam() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncParam>.", Logger.Category.PARSER);
        }
        final var beginnningPosition = lexer.beginningPosition();

        parse:
        {
            final var basicType = parseBasicType();
            if (basicType.isEmpty()) break parse;

            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
            if (leftBracketToken.isEmpty()) return Optional.of(new FuncParam(
                    basicType.get(), identifierToken.get(), Optional.empty(),
                    Optional.empty(), Collections.emptyList()
            ));

            final var rightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);
            if (rightBracketToken.isEmpty()) break parse;

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (lexer.isMatchedTokenOf(LeftBracketToken.class)) {
                final var additionalLeftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (additionalLeftBracketToken.isEmpty()) break;

                final var constExpression = parseConstExpression();
                if (constExpression.isEmpty()) break;

                final var additionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);

                bracketWithConstExpressionList.add(new BracketWith<>(
                        additionalLeftBracketToken.get(), constExpression.get(), additionalRightBracketToken
                ));
            }

            final var result = new FuncParam(
                    basicType.get(), identifierToken.get(), leftBracketToken,
                    rightBracketToken, bracketWithConstExpressionList
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncParam>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncParam>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginnningPosition);
        return Optional.empty();
    }

    private Optional<FuncParamList> parseFuncParamList() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncParamList>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstFuncParam = parseFuncParam();
            if (firstFuncParam.isEmpty()) break parse;

            final var commaWithFuncParamList = new ArrayList<Pair<CommaToken, FuncParam>>();
            while (true) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var funcParam = parseFuncParam();
                if (funcParam.isEmpty()) break parse;

                commaWithFuncParamList.add(new Pair<>(commaToken.get(), funcParam.get()));
            }

            final var result = new FuncParamList(firstFuncParam.get(), commaWithFuncParamList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncParamList>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncParamList>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<FuncType> parseFuncType() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <FuncType>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var funcType = lexer.tryMatchAndConsumeTokenOf(FuncTypeTokenType.class);
            if (funcType.isEmpty()) break parse;

            final var result = new FuncType(funcType.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <FuncType>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <FuncType>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfIfStatement() {
        return lexer.isMatchedTokenOf(IfToken.class);
    }

    private Optional<IfStatement> parseIfStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <IfStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var ifToken = lexer.tryMatchAndConsumeTokenOf(IfToken.class);
            if (ifToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var condition = parseCondition();
            if (condition.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);

            final var ifStatement = parseStatement();
            if (ifStatement.isEmpty()) break parse;

            final var elseToken = lexer.tryMatchAndConsumeTokenOf(ElseToken.class);
            if (elseToken.isEmpty()) {
                final var result = new IfStatement(
                        ifToken.get(), leftParenthesisToken.get(),
                        condition.get(), rightParenthesisToken,
                        ifStatement.get(),
                        Optional.empty(), Optional.empty()
                );
                if (rightParenthesisToken.isEmpty()) {
                    final var error = new MissingRightParenthesisError(
                            result.lastTerminator().endingPosition().lineNumber()
                    );
                    errorHandler.reportError(error);
                }
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <IfStatement>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }

            final var elseStatement = parseStatement();
            if (elseStatement.isEmpty()) break parse;

            final var result = new IfStatement(
                    ifToken.get(), leftParenthesisToken.get(),
                    condition.get(), rightParenthesisToken,
                    ifStatement.get(), elseToken, elseStatement
            );
            if (rightParenthesisToken.isEmpty()) {
                final var error = new MissingRightParenthesisError(
                        result.lastTerminator().endingPosition().lineNumber()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <IfStatement>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <IfStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<LeftValue> parseLeftValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <LeftValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var bracketWithExpressionList = new ArrayList<BracketWith<Expression>>();
            while (true) {
                final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (leftBracketToken.isEmpty()) break;

                final var expression = parseExpression();
                if (expression.isEmpty()) break parse;

                final var optionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);

                bracketWithExpressionList.add(new BracketWith<>(
                        leftBracketToken.get(),
                        expression.get(),
                        optionalRightBracketToken
                ));
            }

            final var result = new LeftValue(identifierToken.get(), bracketWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <LeftValue>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <LeftValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<LogicalAndExpression> parseLogicalAndExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <LogicalAndExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstEqualityExpression = parseEqualityExpression();
            if (firstEqualityExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalAndToken, EqualityExpression>>();
            while (lexer.isMatchedTokenOf(LogicalAndToken.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(LogicalAndToken.class);
                if (operator.isEmpty()) break;

                final var additionalEqualityExpression = parseEqualityExpression();
                if (additionalEqualityExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalEqualityExpression.get()));
            }

            final var result = new LogicalAndExpression(firstEqualityExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <LogicalAndExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <LogicalAndExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<LogicalOrExpression> parseLogicalOrExpression(


    ) {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <LogicalOrExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var firstLogicalAndExpression = parseLogicalAndExpression();
            if (firstLogicalAndExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<LogicalOrToken, LogicalAndExpression>>();
            while (lexer.isMatchedTokenOf(LogicalOrToken.class)) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(LogicalOrToken.class);
                if (operator.isEmpty()) break;

                final var additionalLogicalAndExpression = parseLogicalAndExpression();
                if (additionalLogicalAndExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalLogicalAndExpression.get()));
            }

            final var result = new LogicalOrExpression(firstLogicalAndExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <LogicalOrExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <LogicalOrExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<MainFuncDefinition> parseMainFuncDefinition() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <MainFuncDefinition>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var intToken = lexer.tryMatchAndConsumeTokenOf(IntToken.class);
            if (intToken.isEmpty()) break parse;

            final var mainToken = lexer.tryMatchAndConsumeTokenOf(MainToken.class);
            if (mainToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var block = parseBlock();
            if (block.isEmpty()) break parse;

            final var result = new MainFuncDefinition(
                    intToken.get(),
                    mainToken.get(),
                    leftParenthesisToken.get(),
                    rightParenthesisToken.get(),
                    block.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <MainFuncDefinition>:\n" + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <MainFuncDefinition>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<MultiplicativeExpression> parseMultiplicativeExpression(
    ) {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <MultiplicativeExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var unaryExpression = parseUnaryExpression();
            if (unaryExpression.isEmpty()) break parse;

            final var operatorWithExpressionList = new ArrayList<Pair<MultiplicativeTokenType, UnaryExpression>>();
            while (true) {
                final var operator = lexer.tryMatchAndConsumeTokenOf(MultiplicativeTokenType.class);
                if (operator.isEmpty()) break;

                final var additionalUnaryExpression = parseUnaryExpression();
                if (additionalUnaryExpression.isEmpty()) break parse;

                operatorWithExpressionList.add(new Pair<>(operator.get(), additionalUnaryExpression.get()));
            }

            final var result = new MultiplicativeExpression(unaryExpression.get(), operatorWithExpressionList);
            if (Logger.LogEnabled) {
                Logger.debug("Matched <MultiplicativeExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <MultiplicativeExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ParenthesisedPrimeExpression> parseParenthesisedPrimeExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ParenthesisedPrimeExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var expression = parseExpression();
            if (expression.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var result = new ParenthesisedPrimeExpression(
                    leftParenthesisToken.get(),
                    expression.get(),
                    rightParenthesisToken.get()
            );
            if (Logger.LogEnabled) {
                Logger.debug(
                        "Matched <ParenthesisedPrimeExpression>: " + result.representation(),
                        Logger.Category.PARSER
                );
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ParenthesisedPrimeExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ScalarConstInitValue> parseScalarConstInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ScalarConstInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var constExpression = parseConstExpression();
            if (constExpression.isEmpty()) break parse;

            final var result = new ScalarConstInitValue(constExpression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ScalarConstInitValue>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ScalarConstInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<ScalarVarInitValue> parseScalarVarInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <ScalarVarInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var expression = parseExpression();
            if (expression.isEmpty()) break parse;

            final var result = new ScalarVarInitValue(expression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <ScalarVarInitValue>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <ScalarVarInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfStatement() {
        return lexer.isMatchedTokenOf(IdentifierToken.class)
                || isMatchedBeginningTokenOfExpressionStatementToken()
                || isMatchedBeginningTokenOfBlock()
                || isMatchedBeginningTokenOfIfStatement()
                || isMatchedBeginningTokenOfForLoopStatement()
                || isMatchedBeginningTokenOfBreakStatement()
                || isMatchedBeginningTokenOfContinueStatement()
                || isMatchedBeginningTokenOfReturnStatement()
                || isMatchedBeginningTokenOfPrintfStatement();
    }

    private Optional<Statement> parseStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Statement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(IdentifierToken.class)) {
            final var expressionStatement = parseExpressionStatement();
            if (expressionStatement.isPresent()) {
                final var result = new Statement(expressionStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }

            final var getIntStatement = parseGetIntStatement();
            if (getIntStatement.isPresent()) {
                final var result = new Statement(getIntStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }

            final var assignmentStatement = parseAssignmentStatement();
            if (assignmentStatement.isPresent()) {
                final var result = new Statement(assignmentStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfExpressionStatementToken()) {
            final var expressionStatement = parseExpressionStatement();
            if (expressionStatement.isPresent()) {
                final var result = new Statement(expressionStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfBlock()) {
            final var block = parseBlock();
            if (block.isPresent()) {
                final var result = new Statement(block.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfIfStatement()) {
            final var ifStatement = parseIfStatement();
            if (ifStatement.isPresent()) {
                final var result = new Statement(ifStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfForLoopStatement()) {
            final var forStatementSelection = parseForLoopStatement();
            if (forStatementSelection.isPresent()) {
                final var result = new Statement(forStatementSelection.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>:\n" + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfBreakStatement()) {
            final var breakStatement = parseBreakStatement();
            if (breakStatement.isPresent()) {
                final var result = new Statement(breakStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfContinueStatement()) {
            final var optionalContinueStatement = parseContinueStatement();
            if (optionalContinueStatement.isPresent()) {
                final var continueStatement = optionalContinueStatement.get();
                final var result = new Statement(continueStatement);
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfReturnStatement()) {
            final var returnStatement = parseReturnStatement();
            if (returnStatement.isPresent()) {
                final var result = new Statement(returnStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfPrintfStatement()) {
            final var printfStatement = parsePrintfStatement();
            if (printfStatement.isPresent()) {
                final var result = new Statement(printfStatement.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <Statement>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Statement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<SysYNumber> parseNumber() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <Number>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var literalIntegerToken = lexer.tryMatchAndConsumeTokenOf(LiteralIntegerToken.class);
            if (literalIntegerToken.isEmpty()) break parse;

            final var result = new SysYNumber(literalIntegerToken.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <Number>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <Number>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<UnaryExpression> parseUnaryExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <UnaryExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (isMatchedBeginningTokensOfFuncInvocation()) {
            final var funcInvocation = parseFuncInvocation();
            if (funcInvocation.isPresent()) {
                final var result = new UnaryExpression(funcInvocation.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <UnaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfPrimaryExpression()) {
            final var primaryExpression = parsePrimaryExpression();
            if (primaryExpression.isPresent()) {
                final var result = new UnaryExpression(primaryExpression.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <UnaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (isMatchedBeginningTokenOfUnaryOperatedExpression()) {
            final var unaryOperatedExpression = parseUnaryOperatedExpression();
            if (unaryOperatedExpression.isPresent()) {
                final var result = new UnaryExpression(unaryOperatedExpression.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <UnaryExpression>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <UnaryExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<UnaryOperator> parseUnaryOperator() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <UnaryOperator>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var operator = lexer.tryMatchAndConsumeTokenOf(UnaryOperatorTokenType.class);
            if (operator.isEmpty()) break parse;

            final var result = new UnaryOperator(operator.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <UnaryOperator>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <UnaryOperator>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokensOfVarDeclaration() {
        final var beginningPosition = lexer.beginningPosition();
        final var intermediate = lexer.tryMatchAndConsumeTokenOf(BasicTypeTokenType.class)
                .flatMap(t -> lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class));
        lexer.resetPosition(beginningPosition);
        return intermediate.isPresent() && !lexer.isMatchedTokenOf(LeftParenthesisToken.class);
    }

    private Optional<VarDeclaration> parseVarDeclaration() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <VarDeclaration>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var basicType = parseBasicType();
            if (basicType.isEmpty()) break parse;

            final var firstVarDefinition = parseVarDefinition();
            if (firstVarDefinition.isEmpty()) break parse;

            final var additionalVarDefinitionList = new ArrayList<Pair<CommaToken, VarDefinition>>();
            while (lexer.isMatchedTokenOf(CommaToken.class)) {
                final var commaToken = lexer.tryMatchAndConsumeTokenOf(CommaToken.class);
                if (commaToken.isEmpty()) break;

                final var varDefinition = parseVarDefinition();
                if (varDefinition.isEmpty()) break parse;

                additionalVarDefinitionList.add(new Pair<>(commaToken.get(), varDefinition.get()));
            }

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            if (semicolonToken.isEmpty()) {
                if (lexer.isMatchedTokenOf(LeftParenthesisToken.class)) break parse;
            } // Avoid Matching FuncDef.

            final var result = new VarDeclaration(
                    basicType.get(),
                    firstVarDefinition.get(),
                    additionalVarDefinitionList,
                    semicolonToken
            );
            if (semicolonToken.isEmpty()) {
                final var lastToken = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastToken.endingPosition()),
                        lastToken.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <VarDeclaration>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <VarDeclaration>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<VarDefinition> parseVarDefinition() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <VarDefinition>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var identifierToken = lexer.tryMatchAndConsumeTokenOf(IdentifierToken.class);
            if (identifierToken.isEmpty()) break parse;

            final var bracketWithConstExpressionList = new ArrayList<BracketWith<ConstExpression>>();
            while (lexer.isMatchedTokenOf(LeftBracketToken.class)) {
                final var leftBracketToken = lexer.tryMatchAndConsumeTokenOf(LeftBracketToken.class);
                if (leftBracketToken.isEmpty()) break;

                final var constExpression = parseConstExpression();
                if (constExpression.isEmpty()) break parse;

                final var optionalRightBracketToken = lexer.tryMatchAndConsumeTokenOf(RightBracketToken.class);
                if (optionalRightBracketToken.isEmpty()) Logger.warn("Tolerated a right bracket missing.");

                bracketWithConstExpressionList.add(new BracketWith<>(
                        leftBracketToken.get(),
                        constExpression.get(),
                        optionalRightBracketToken
                ));
            }

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) {
                final var result = new VarDefinition(
                        identifierToken.get(), bracketWithConstExpressionList, Optional.empty(), Optional.empty()
                );
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <VarDefinition>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }

            final var varInitValue = parseVarInitValue();
            if (varInitValue.isEmpty()) break parse;

            final var result = new VarDefinition(
                    identifierToken.get(), bracketWithConstExpressionList, assignToken, varInitValue
            );
            if (Logger.LogEnabled) {
                Logger.debug("Matched <VarDefinition>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <VarDefinition>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<VarInitValue> parseVarInitValue() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <VarInitValue>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        if (lexer.isMatchedTokenOf(LeftBraceToken.class)) {
            final var arrayVarInitValue = parseArrayVarInitValue();
            if (arrayVarInitValue.isPresent()) {
                final var result = new VarInitValue(arrayVarInitValue.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <VarInitValue>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        } else {
            final var scalarVarInitValue = parseScalarVarInitValue();
            if (scalarVarInitValue.isPresent()) {
                final var result = new VarInitValue(scalarVarInitValue.get());
                if (Logger.LogEnabled) {
                    Logger.debug("Matched <VarInitValue>: " + result.representation(), Logger.Category.PARSER);
                }
                return Optional.of(result);
            }
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <VarInitValue>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private boolean isMatchedBeginningTokenOfUnaryOperatedExpression() {
        return lexer.isMatchedTokenOf(UnaryOperatorTokenType.class);
    }

    private Optional<UnaryOperatedExpression> parseUnaryOperatedExpression() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <UnaryOperatedExpression>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var unaryOperator = parseUnaryOperator();
            if (unaryOperator.isEmpty()) break parse;

            final var unaryExpression = parseUnaryExpression();
            if (unaryExpression.isEmpty()) break parse;

            final var result = new UnaryOperatedExpression(unaryOperator.get(), unaryExpression.get());
            if (Logger.LogEnabled) {
                Logger.debug("Matched <UnaryOperatedExpression>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <UnaryOperatedExpression>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }

    private Optional<GetIntStatement> parseGetIntStatement() {
        if (Logger.LogEnabled) {
            Logger.debug("Matching <GetIntStatement>.", Logger.Category.PARSER);
        }
        final var beginningPosition = lexer.beginningPosition();

        parse:
        {
            final var leftValue = parseLeftValue();
            if (leftValue.isEmpty()) break parse;

            final var assignToken = lexer.tryMatchAndConsumeTokenOf(AssignToken.class);
            if (assignToken.isEmpty()) break parse;

            final var getIntToken = lexer.tryMatchAndConsumeTokenOf(GetIntToken.class);
            if (getIntToken.isEmpty()) break parse;

            final var leftParenthesisToken = lexer.tryMatchAndConsumeTokenOf(LeftParenthesisToken.class);
            if (leftParenthesisToken.isEmpty()) break parse;

            final var rightParenthesisToken = lexer.tryMatchAndConsumeTokenOf(RightParenthesisToken.class);
            if (rightParenthesisToken.isEmpty()) break parse;

            final var semicolonToken = lexer.tryMatchAndConsumeTokenOf(SemicolonToken.class);

            final var result = new GetIntStatement(
                    leftValue.get(),
                    assignToken.get(),
                    getIntToken.get(),
                    leftParenthesisToken.get(),
                    rightParenthesisToken.get(),
                    semicolonToken
            );
            if (semicolonToken.isEmpty()) {
                final var lastTerminator = result.lastTerminator();
                final var error = new MissingSemicolonError(
                        lexer.getLineAt(lastTerminator.endingPosition()),
                        lastTerminator.endingPosition()
                );
                errorHandler.reportError(error);
            }
            if (Logger.LogEnabled) {
                Logger.debug("Matched <GetIntStatement>: " + result.representation(), Logger.Category.PARSER);
            }
            return Optional.of(result);
        }

        if (Logger.LogEnabled) {
            Logger.debug("Failed to match <GetIntStatement>.", Logger.Category.PARSER);
        }
        lexer.resetPosition(beginningPosition);
        return Optional.empty();
    }
}
