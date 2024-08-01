package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class BlockPat implements Pattern {
    static BlockPat instance = new BlockPat();

    @Override
    public Op parse(Parser parser) {
        var doPos = parser.peek().pos();
        parser.expect(TokenKind.DO, "expected block while parsing");

        var peek = parser.peekRaw();
        if (peek.line() == parser.line) {
            return parser.pattern(StmtPat.instance);
        }
        if (peek.indent() <= parser.indent) {
            throw new LangError(peek.pos(), "expected indented block while parsing");
        }

        var indentOld = parser.indent;
        parser.indent = peek.indent();
        parser.line = peek.line();

        var block = parser.withIndentSensitivity(true, pInner -> {
            var stmts = new ArrayList<Op>();
            
            while (true) {
                switch (pInner.peek().kind()) {
                    case TokenKind.INDENT, TokenKind.EOF -> {}
                    default -> {
                        var stmt = pInner.pattern(StmtPat.instance);
                        stmts.add(stmt);

                        var newline = parser.peek();
                        if (newline.kind() == TokenKind.INDENT && newline.indent() == parser.indent) {
                            parser.bump();
                            continue;
                        }
                    }
                }

                return new Block(doPos.to(stmts.getLast().pos()), stmts);
            }
        });

        parser.indent = indentOld;
        return block;
    }
}
