package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class BlockPat implements Pattern {
    private static Logger log = Logger.getGlobal();

    static BlockPat instance = new BlockPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse block");
        
        var peekDo = parser.peek();
        parser.expect(TokenKind.DO, "expected block while parsing");

        var peek = parser.peekRaw();
        if (peek.line() == parser.line) {
            return parser.pattern(StmtPat.instance);
        }
        if (peek.indent() <= parser.indent || peek.indent() <= peekDo.indent()) {
            throw new LangError(peek.pos(), "expected indented block while parsing");
        }

        var indentOld = parser.indent;
        parser.indent = peek.indent();
        parser.line = peek.line();

        try (var indentHandler = parser.new IndentSensitivityHandler(true)) {
            var stmts = new ArrayList<Op>();
            
            while (true) {
                var cont = switch (parser.peek().kind()) {
                    case TokenKind.INDENT -> {
                        yield parser.peek().indent() == parser.indent;
                    }
                    case TokenKind.EOF -> false;
                    default -> {
                        var stmt = parser.pattern(StmtPat.instance);
                        stmts.add(stmt);

                        var newline = parser.peek();
                        if (newline.kind() == TokenKind.INDENT && newline.indent() == parser.indent) {
                            parser.bump();
                            yield true;
                        }

                        if (!newline.isDelim()) {
                            throw new LangError(newline.pos(), "expected new statement while parsing");
                        }

                        yield false;
                    }
                };

                if (cont) {
                    continue;
                }

                parser.indent = indentOld;
                return new Block(peekDo.pos().to(stmts.getLast().pos()), stmts);
            }
        }
    }
}
