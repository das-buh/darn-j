package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class BlockPat implements Pattern<Op> {
    private static Logger log = Logger.getGlobal();

    static final BlockPat instance = new BlockPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse block");
        
        var doToken = parser.peek();
        parser.expect(Token.Kind.DO, "expected block while parsing");

        var blockStart = parser.peekRaw();
        if (blockStart.line() == parser.line) {
            return StmtPat.instance.parse(parser);
        }
        if (blockStart.indent() <= parser.indent || blockStart.indent() <= doToken.indent()) {
            throw new LangError(blockStart.pos(), "expected indented block while parsing");
        }

        var oldIndent = parser.indent;
        parser.indent = blockStart.indent();
        parser.line = blockStart.line();

        try (var indentHandler = parser.new IndentSensitivityHandler(true)) {
            var stmts = new ArrayList<Op>();
            
            while (true) {
                stmts.add(StmtPat.instance.parse(parser));

                var newline = parser.peek();

                if (!newline.isDelim()) {
                    throw new LangError(newline.pos(), "expected new statement while parsing");
                }
                if (newline.kind() == Token.Kind.INDENT && newline.indent() == parser.indent) {
                    parser.bump();
                    continue;
                }

                parser.indent = oldIndent;
                return new Block(doToken.pos().to(stmts.getLast().pos()), stmts);
            }
        }
    }
}
