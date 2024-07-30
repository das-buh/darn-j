package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.lex.*;
import com.darnj.op.*;

final class AtomPat extends Pattern {
    static AtomPat instance = new AtomPat();

    @Override
    Op parse(Parser parser) throws Error {
        var peek = parser.peek(0);
        var pos = peek.pos();
        return switch (peek.kind()) {
            case TokenKind.IDENT -> {
                parser.bump();
                var id = parser.ctx.symbols().intern(parser.src.substring(pos.start(), pos.end()));

                if (parser.peek(0).kind() == TokenKind.PAREN_OPEN) {
                    yield parser.withIndentSensitivity(false, new Pattern() {
                        @Override
                        Op parse(Parser parser) throws Error {
                            var nil = parser.peek(0);
                            if (nil.kind() == TokenKind.PAREN_CLOSE) {
                                parser.bump();
                                return new Call(peek.pos().to(nil.pos()), id, new ArrayList<Op>());
                            }
        
                            var elems = parser.commaSeparated(ExprPat.instance);
                            var close = parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
        
                            return new Call(peek.pos().to(close), id, elems);
                        }
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
                    throw new Error(pos, "invalid integer literal");
                }
            }
            case TokenKind.FLOAT_LITERAL -> {
                parser.bump();
                try {
                    var lit = parser.src.substring(pos.start(), pos.end());
                    yield new FloatConstant(pos, Double.parseDouble(lit));
                } catch (NumberFormatException _) {
                    throw new Error(pos, "invalid float literal");
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
            case TokenKind.NIL -> {
                parser.bump();
                yield new Nil(pos);
            }
            case TokenKind.PAREN_OPEN -> {
                parser.bump();
                yield parser.withIndentSensitivity(false, new Pattern() {
                    @Override
                    Op parse(Parser parser) throws Error {
                        var inner = parser.pattern(ExprPat.instance);
                        var close = parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
                        return new Identity(pos.to(close), inner);
                    }
                });
            }
            case TokenKind.BRACKET_OPEN -> {
                parser.bump();
                yield parser.withIndentSensitivity(false, new Pattern() {
                    @Override
                    Op parse(Parser parser) throws Error {
                        var nil = parser.peek(0);
                        if (nil.kind() == TokenKind.BRACKET_CLOSE) {
                            parser.bump();
                            return new Slice(pos.to(nil.pos()), new ArrayList<Op>());
                        }

                        var elems = parser.commaSeparated(ExprPat.instance);
                        var close = parser.expect(TokenKind.BRACKET_CLOSE, "expected closing bracket while parsing");

                        return new Slice(pos.to(close), elems);
                    }
                });
            }
            default -> throw new Error(peek.pos(), "expected expression while parsing");
        };
    }
}
