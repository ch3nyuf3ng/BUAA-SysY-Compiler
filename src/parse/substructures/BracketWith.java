package parse.substructures;

import lex.token.LeftBracketToken;
import lex.token.RightBracketToken;

import java.util.Optional;

public record BracketWith<T>(
        LeftBracketToken leftBracketToken,
        T entity,
        Optional<RightBracketToken> optionalRightBracketToken
) {
}
