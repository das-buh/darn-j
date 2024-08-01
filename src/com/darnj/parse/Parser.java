package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;

public final class Parser {
    private static Logger log = Logger.getGlobal();

    String src;

    Lexer lexer;

    int pos;
    int indent;
    int line;
    boolean inIndentSensitiveMode;

    Context ctx;

    Parser(String src, Context ctx) {
        this.src = src;
        lexer = new Lexer(src);
        pos = 0;
        indent = 0;
        line = 0;
        inIndentSensitiveMode = true;
        this.ctx = ctx;
    }

    public static Op parse(String src, Context ctx) {
        var parser = new Parser(src, ctx);
        log.fine("parsing");
        var program = parser.pattern(ProgramPat.instance);
        log.fine("parsed");
        return program;
    }

    void bump() {
        var bump = peek();
        if (bump.kind() != TokenKind.INDENT) {
            lexer.bump();
            pos = bump.pos().end();
            line = bump.line();
        } else if (bump.indent() == indent) {
            line = bump.line();
        }
    }

    Token peek() {
        var peek = lexer.peek();
        
        if (inIndentSensitiveMode &&
            peek.line() != line &&
            peek.indent() <= indent &&
            peek.kind() != TokenKind.EOF
        ) {
            return new Token(new Span(pos, pos), peek.indent(), peek.line(), TokenKind.INDENT);
        }

        return peek;
    }

    void bumpRaw() {
        var bump = peek();
        lexer.bump();
        pos = bump.pos().end();
        line = bump.line();
    }

    Token peekRaw() {
        return lexer.peek();
    }

    void expect(TokenKind kind, String message) {
        var peek = peek();
        if (peek.kind() != kind) {
            throw new LangError(peek.pos(), message);
        }
        bump();
    }

    int expectIdent() {
        var peek = peek();
        if (peek.kind() != TokenKind.IDENT) {
            throw new LangError(peek.pos(), "expected identifier while parsing");
        }
        bump();
        var ident = src.substring(peek.pos().start(), peek.pos().end());
        return ctx.symbols().intern(ident);
    }

    Op pattern(Pattern pattern) {
        return pattern.parse(this);
    }

    Op withIndentSensitivity(boolean sensitivity, Pattern pattern) {
        var prev = inIndentSensitiveMode;
        inIndentSensitiveMode = sensitivity;
        var op = pattern(pattern);
        inIndentSensitiveMode = prev;
        return op;
    }

    // Expects one or more comma-separated patterns.
    ArrayList<Op> commaSeparated(Pattern pattern) {
        var items = new ArrayList<Op>();

        while (true) {
            var elem = pattern(pattern);
            items.add(elem);

            switch (peek().kind()) {
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
