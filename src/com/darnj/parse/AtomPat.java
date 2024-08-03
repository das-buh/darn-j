package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class AtomPat implements Pattern<Op> {
    private static Logger log = Logger.getGlobal();

    static final AtomPat instance = new AtomPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse atom");

        var peek = parser.peek();
        var pos = peek.pos();
        return switch (peek.kind()) {
            case TokenKind.IDENT -> parseVarOrCall(parser);
            case TokenKind.INT_LITERAL -> {
                parser.bump();
                try {
                    var lit = parser.src.substring(pos.start(), pos.end());
                    yield new IntConstant(pos, Long.parseLong(lit));
                } catch (NumberFormatException _) {
                    throw new LangError(pos, "invalid integer literal");
                }
            }
            case TokenKind.FLOAT_LITERAL -> {
                parser.bump();
                try {
                    var lit = parser.src.substring(pos.start(), pos.end());
                    yield new FloatConstant(pos, Double.parseDouble(lit));
                } catch (NumberFormatException _) {
                    throw new LangError(pos, "invalid float literal");
                }
            }
            case TokenKind.STR_LITERAL -> {
                parser.bump();
                yield new StrConstant(pos, parser.src.substring(pos.start() + 1, pos.end() - 1));
            }
            case TokenKind.TRUE -> {
                parser.bump();
                yield new True(pos);
            }
            case TokenKind.FALSE -> {
                parser.bump();
                yield new False(pos);
            }
            case TokenKind.NIL -> {
                parser.bump();
                yield new Nil(pos);
            }
            case TokenKind.PAREN_OPEN -> parseParentheses(parser);
            case TokenKind.BRACKET_OPEN -> parseList(parser);
            default -> throw new LangError(peek.pos(), "expected expression while parsing");
        };
    }

    private Op parseVarOrCall(Parser parser) {
        var variable = parser.peek();
        parser.bump();

        var src = parser.src.substring(variable.pos().start(), variable.pos().end());
        var id = parser.ctx.symbols().intern(src);

        if (parser.peek().kind() == TokenKind.PAREN_OPEN) {
            parser.bump();
            
            try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
                var closeZero = parser.peek();
                if (closeZero.kind() == TokenKind.PAREN_CLOSE) {
                    parser.bump();
                    return new Call(variable.pos().to(closeZero.pos()), variable.pos(), id, new ArrayList<>());
                }

                var elems = parser.commaSeparated(ExprPat.instance);
                var close = parser.peek().pos();
                parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");

                return new Call(variable.pos().to(close), variable.pos(), id, elems);
            }
        }

        return new Variable(variable.pos(), id);
    }

    private Op parseParentheses(Parser parser) {
        var open = parser.peek();
        parser.bump();

        try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
            var inner = ExprPat.instance.parse(parser);
            var close = parser.peek().pos();
            parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
            return new Identity(open.pos().to(close), inner);
        }
    }

    private Op parseList(Parser parser) {
        var open = parser.peek();
        parser.bump();

        try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
            var closeZero = parser.peek();
            if (closeZero.kind() == TokenKind.BRACKET_CLOSE) {
                parser.bump();
                return new List(open.pos().to(closeZero.pos()), new ArrayList<>());
            }

            var elems = parser.commaSeparated(ExprPat.instance);
            var close = parser.peek().pos();
            parser.expect(TokenKind.BRACKET_CLOSE, "expected closing bracket while parsing");

            return new List(open.pos().to(close), elems);
        }
    }
}
