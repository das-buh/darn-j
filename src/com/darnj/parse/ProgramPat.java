package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;
import com.darnj.type.*;

final class ProgramPat implements Pattern {
    static ProgramPat instance = new ProgramPat();

    @Override
    public Op parse(Parser parser) {
        var stmts = new ArrayList<Op>();

        while (true) {
            switch (parser.peek().kind()) {
                case TokenKind.FN -> {
                    parser.bump();
                    parseFunction(parser);
                }
                case TokenKind.EOF -> {
                    return new Block(new Span(0, parser.src.length()), stmts);
                }
                default -> {
                    var stmt = parser.pattern(StmtPat.instance);
                    stmts.add(stmt);

                    switch (parser.peek().kind()) {
                        case TokenKind.INDENT, TokenKind.EOF -> {
                            parser.bump();
                        }
                        default -> throw new LangError(parser.peek().pos(), "expected newline while parsing");
                    }
                }
            }
        }
    }

    // Returns null if parsed a function declaration.
    void parseFunction(Parser parser) {
        var funcPos = parser.peek().pos();
        var func = parser.expectIdent();

        parser.expect(TokenKind.PAREN_OPEN, "expected opening parenthesis while parsing");
        parser.withIndentSensitivity(false, pInner -> {
            var params = new ArrayList<Param>();
            if (pInner.peek().kind() == TokenKind.PAREN_CLOSE) {
                pInner.bump();
            } else while (true) {
                var param = pInner.expectIdent();
                var type = parseType(pInner);
                params.add(new Param(param, type));
    
                var delim = pInner.peek();
                switch (delim.kind()) {
                    case TokenKind.COMMA -> {
                        pInner.bump();
                        continue;
                    }
                    case TokenKind.PAREN_CLOSE -> {
                        pInner.bump();
                        break;
                    }
                    default -> throw new LangError(delim.pos(), "expected closing parenthesis while parsing");
                }
            };

            var returnType = switch (pInner.peek().kind()) {
                case TokenKind.DO -> {
                    pInner.bump();
                    yield UndefinedType.instance();
                }
                default -> parseType(pInner);
            };

            var body = pInner.pattern(BlockPat.instance);

            var funcs = pInner.ctx.funcs();
            if (funcs.containsKey(func)) {
                throw new LangError(funcPos, "function `" + pInner.ctx.symbols().resolve(func) + "` is already defined");
            }

            funcs.put(func, new UserFunction(func, funcPos, params, returnType, body));
            return null;
        });
    }
    
    static Type parseType(Parser parser) {
        var peek = parser.peek();
        
        var type = switch (peek.kind()) {
            case TokenKind.INT -> IntType.instance();
            case TokenKind.FLOAT -> FloatType.instance();
            case TokenKind.BOOL -> BoolType.instance();
            case TokenKind.STR -> StrType.instance();
            case TokenKind.LIST -> StrType.instance();
            default -> throw new LangError(peek.pos(), "expected type while parsing");
        };
        parser.bump();

        while (true) {
            var cons = switch (peek.kind()) {
                case TokenKind.QMARK -> {
                    var optional = type.optional();
                    if (optional == null) {
                        throw new LangError(peek.pos(), "type cannot be doubly nil-able");
                    }
                    yield optional;
                }
                case TokenKind.STAR -> type.reference();
                default -> null;
            };

            if (cons == null) {
                return type;
            }

            parser.bump();
            type = cons;
        }
    }
}
