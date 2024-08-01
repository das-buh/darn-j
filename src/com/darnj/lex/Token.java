package com.darnj.lex;

import com.darnj.Span;

public final record Token(Span pos, int indent, int line, TokenKind kind) {
    public boolean isDelim() {
        return switch (kind) {        
            case COMMA, PAREN_CLOSE, BRACKET_CLOSE, INDENT, EOF -> true;
            default -> false;
        };
    }
}
