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
            case Token.Kind.IDENT -> parseVarOrCall(parser);
            case Token.Kind.INT_LITERAL -> {
                parser.bump();
                try {
                    var lit = parser.src.substring(pos.start(), pos.end());
                    yield new IntConstant(pos, Long.parseLong(lit));
                } catch (NumberFormatException _) {
                    throw new LangError(pos, "invalid integer literal");
                }
            }
            case Token.Kind.FLOAT_LITERAL -> {
                parser.bump();
                try {
                    var lit = parser.src.substring(pos.start(), pos.end());
                    yield new FloatConstant(pos, Double.parseDouble(lit));
                } catch (NumberFormatException _) {
                    throw new LangError(pos, "invalid float literal");
                }
            }
            case Token.Kind.STR_LITERAL -> {
                parser.bump();
                yield new StrConstant(pos, parser.src.substring(pos.start() + 1, pos.end() - 1));
            }
            case Token.Kind.TRUE -> {
                parser.bump();
                yield new True(pos);
            }
            case Token.Kind.FALSE -> {
                parser.bump();
                yield new False(pos);
            }
            case Token.Kind.NIL -> {
                parser.bump();
                yield new Nil(pos);
            }
            case Token.Kind.PAREN_OPEN -> parseParentheses(parser);
            case Token.Kind.BRACKET_OPEN -> parseList(parser);
            default -> throw new LangError(peek.pos(), "expected expression while parsing");
        };
    }

    private Op parseVarOrCall(Parser parser) {
        var variable = parser.peek();
        parser.bump();

        var src = parser.src.substring(variable.pos().start(), variable.pos().end());
        var id = parser.ctx.symbols().intern(src);

        if (parser.peek().kind() == Token.Kind.PAREN_OPEN) {
            parser.bump();
            
            try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
                var closeZero = parser.peek();
                if (closeZero.kind() == Token.Kind.PAREN_CLOSE) {
                    parser.bump();
                    return new Call(variable.pos().to(closeZero.pos()), variable.pos(), id, new ArrayList<>());
                }

                var elems = parser.commaSeparated(ExprPat.instance);
                var close = parser.peek().pos();
                parser.expect(Token.Kind.PAREN_CLOSE, "expected closing parenthesis while parsing");

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
            parser.expect(Token.Kind.PAREN_CLOSE, "expected closing parenthesis while parsing");
            return new Identity(open.pos().to(close), inner);
        }
    }

    private Op parseList(Parser parser) {
        var open = parser.peek();
        parser.bump();

        try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
            var closeZero = parser.peek();
            if (closeZero.kind() == Token.Kind.BRACKET_CLOSE) {
                parser.bump();
                return new List(open.pos().to(closeZero.pos()), new ArrayList<>());
            }

            var elems = parser.commaSeparated(ExprPat.instance);
            var close = parser.peek().pos();
            parser.expect(Token.Kind.BRACKET_CLOSE, "expected closing bracket while parsing");

            return new List(open.pos().to(close), elems);
        }
    }
}
