package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

final class AtomPat implements Pattern {
    static AtomPat instance = new AtomPat();

    @Override
    public Op parse(Parser parser) {
        var peek = parser.peek();
        var pos = peek.pos();
        return switch (peek.kind()) {
            case TokenKind.IDENT -> {
                parser.bump();
                var id = parser.ctx.symbols().intern(parser.src.substring(pos.start(), pos.end()));

                if (parser.peek().kind() == TokenKind.PAREN_OPEN) {
                    yield parser.withIndentSensitivity(false, pInner -> {
                        var nil = pInner.peek();
                        if (nil.kind() == TokenKind.PAREN_CLOSE) {
                            pInner.bump();
                            return new Call(peek.pos().to(nil.pos()), peek.pos(), id, new ArrayList<>());
                        }
    
                        var elems = pInner.commaSeparated(ExprPat.instance);
                        var close = pInner.peek().pos();
                        pInner.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
    
                        return new Call(peek.pos().to(close), peek.pos(), id, elems);
                    });
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
                yield new StrConstant(pos, parser.src.substring(pos.start(), pos.end()));
            }
            case TokenKind.TRUE -> {
                parser.bump();
                yield new True(pos);
            }
            case TokenKind.FALSE -> {
                parser.bump();
                yield new False(pos);
            }
            case TokenKind.PAREN_OPEN -> {
                parser.bump();
                yield parser.withIndentSensitivity(false, pInner -> {
                    var inner = pInner.pattern(ExprPat.instance);
                    var close = pInner.peek().pos();
                    pInner.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
                    return new Identity(pos.to(close), inner);
                });
            }
            case TokenKind.BRACKET_OPEN -> {
                parser.bump();
                yield parser.withIndentSensitivity(false, pInner -> {
                    var nil = pInner.peek();
                    if (nil.kind() == TokenKind.BRACKET_CLOSE) {
                        pInner.bump();
                        return new List(pos.to(nil.pos()), new ArrayList<>());
                    }

                    var elems = pInner.commaSeparated(ExprPat.instance);
                    var close = pInner.peek().pos();
                    pInner.expect(TokenKind.BRACKET_CLOSE, "expected closing bracket while parsing");

                    return new List(pos.to(close), elems);
                });
            }
            default -> throw new LangError(peek.pos(), "expected expression while parsing");
        };
    }
}
