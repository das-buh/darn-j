package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class AtomPat implements Pattern {
    private static Logger log = Logger.getGlobal();

    static AtomPat instance = new AtomPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse atom");

        var peek = parser.peek();
        var pos = peek.pos();
        return switch (peek.kind()) {
            case TokenKind.IDENT -> {
                parser.bump();
                var id = parser.ctx.symbols().intern(parser.src.substring(pos.start(), pos.end()));

                if (parser.peek().kind() == TokenKind.PAREN_OPEN) {
                    parser.bump();
                    
                    try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
                        var nil = parser.peek();
                        if (nil.kind() == TokenKind.PAREN_CLOSE) {
                            parser.bump();
                            yield new Call(peek.pos().to(nil.pos()), peek.pos(), id, new ArrayList<>());
                        }
    
                        var elems = parser.commaSeparated(ExprPat.instance);
                        var close = parser.peek().pos();
                        parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
    
                        yield new Call(peek.pos().to(close), peek.pos(), id, elems);
                    }
                }

                yield new Variable(pos, id);
            }
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
            case TokenKind.PAREN_OPEN -> {
                parser.bump();
                try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
                    var inner = parser.pattern(ExprPat.instance);
                    var close = parser.peek().pos();
                    parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
                    yield new Identity(pos.to(close), inner);
                }
            }
            case TokenKind.BRACKET_OPEN -> {
                parser.bump();

                try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
                    var nil = parser.peek();
                    if (nil.kind() == TokenKind.BRACKET_CLOSE) {
                        parser.bump();
                        yield new List(pos.to(nil.pos()), new ArrayList<>());
                    }

                    var elems = parser.commaSeparated(ExprPat.instance);
                    var close = parser.peek().pos();
                    parser.expect(TokenKind.BRACKET_CLOSE, "expected closing bracket while parsing");

                    yield new List(pos.to(close), elems);
                }
            }
            default -> throw new LangError(peek.pos(), "expected expression while parsing");
        };
    }
}
