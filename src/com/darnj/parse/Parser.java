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

    final String src;

    final Lexer lexer;

    int pos;
    int indent;
    int line;
    boolean inIndentMode;

    final Context ctx;

    Parser(String src, Context ctx) {
        this.src = src;
        lexer = new Lexer(src);
        pos = 0;
        indent = 0;
        line = 0;
        inIndentMode = true;
        this.ctx = ctx;
    }

    public static Op parse(String src, Context ctx) {
        var parser = new Parser(src, ctx);
        log.fine("parse");
        var program = parser.pattern(ProgramPat.instance);
        log.fine("parse done");
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
        
        if (inIndentMode &&
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

    // Expects one or more comma-separated patterns.
    ArrayList<Op> commaSeparated(Pattern pattern) {
        var items = new ArrayList<Op>();

        while (true) {
            var elem = pattern(pattern);
            items.add(elem);

            var term = switch (peek().kind()) {
                case TokenKind.COMMA -> {
                    bump();
                    yield false;
                }
                default -> true;
            };

            if (term) {
                return items;
            }
        }
    }

    protected class IndentSensitivityHandler implements AutoCloseable {
        private boolean oldIndentMode;

        protected IndentSensitivityHandler(boolean newIndentMode) {
            oldIndentMode = inIndentMode;
            inIndentMode = newIndentMode;
        }

        @Override
        public void close() {
            inIndentMode = oldIndentMode;
        }
    }
}
