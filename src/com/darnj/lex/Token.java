package com.darnj.lex;

import com.darnj.Span;

public final record Token(Span pos, int indent, int line, TokenKind kind) {}
