package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;

public final class Parser {
    String src;

    private Lexer lexer;

    int pos;
    int indent;
    int line;
    boolean inIndentSensitiveMode;

    Context ctx;

    Parser(String src, Context ctx) throws Error {
        this.src = src;
        lexer = new Lexer(src);
        pos = 0;
        indent = 0;
        line = 0;
        inIndentSensitiveMode = true;
        this.ctx = ctx;
    }

    public Op parse(String src, Context ctx) throws Error {
        var parser = new Parser(src, ctx);
    }

    void bump() throws Error {
        var bump = peek(0);
        if (bump.kind() != TokenKind.INDENT) {
            lexer.bump();
            pos = bump.pos().end();
            line = bump.line();
        }
    }

    Token peek(int idx) {
        var peek = lexer.peek(idx);
        if (inIndentSensitiveMode && peek.line() != line && peek.indent() <= indent) {
            var endline = idx == 0 ? pos : lexer.peek(idx - 1).pos().end();
            var span = new Span(endline, endline);
            return new Token(span, peek.indent(), peek.line(), TokenKind.INDENT);
        }
        return peek;
    }

    Span expect(TokenKind kind, String message) throws Error {
        var peek = peek(0);
        if (peek.kind() != kind) {
            throw new Error(peek.pos(), message);
        }
        bump();
        return peek.pos();
    }

    int expectIdent() throws Error {
        var peek = peek(0);
        if (peek.kind() != TokenKind.IDENT) {
            throw new Error(peek.pos(), "expected identifier while parsing");
        }
        bump();
        var ident = src.substring(peek.pos().start(), peek.pos().end());
        return ctx.symbols().intern(ident);
    }

    Op pattern(Pattern pattern) throws Error {
        return pattern.parse(this);
    }

    Op withIndentSensitivity(boolean sensitivity, Pattern pattern) throws Error {
        var prev = inIndentSensitiveMode;
        inIndentSensitiveMode = sensitivity;
        var op = pattern(pattern);
        inIndentSensitiveMode = prev;
        return op;
    }

    // Expects one or more comma-separated patterns.
    ArrayList<Op> commaSeparated(Pattern pattern) throws Error {
        var items = new ArrayList<Op>();

        while (true) {
            var elem = pattern(pattern);
            items.add(elem);

            switch (peek(0).kind()) {
                case TokenKind.COMMA -> {
                    bump();
                    continue;
                }
                default -> {
                    return items;
                }
            }
        }
    }
}
