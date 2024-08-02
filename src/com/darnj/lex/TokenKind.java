package com.darnj.lex;

public enum TokenKind {
    IDENT,
    INT_LITERAL,
    FLOAT_LITERAL,
    STR_LITERAL,

    TRUE,
    FALSE,
    NIL,

    IF,
    ELSE,
    WHILE,

    CONTINUE,
    BREAK,
    RETURN,

    DO,
    FN,
    ASSIGN,

    INT,
    FLOAT,
    BOOL,
    STR,
    LIST,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    MODULO,
    REF,

    ADD_ASSIGN,
    SUB_ASSIGN,
    MUL_ASSIGN,
    DIV_ASSIGN,
    MOD_ASSIGN,

    NOT,
    AND,
    OR,

    EQ,
    NEQ,
    LT,
    LTE,
    GT,
    GTE,

    QMARK,

    COMMA,
    PAREN_OPEN,
    PAREN_CLOSE,
    BRACKET_OPEN,
    BRACKET_CLOSE,

    INDENT,
    EOF,
}
