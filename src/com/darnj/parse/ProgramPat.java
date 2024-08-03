package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;
import com.darnj.type.*;

final class ProgramPat implements Pattern {
    private static Logger log = Logger.getGlobal();

    static ProgramPat instance = new ProgramPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse program");
        
        var stmts = new ArrayList<Op>();

        while (true) {
            var term = switch (parser.peek().kind()) {
                case TokenKind.FN -> {
                    parser.bump();
                    parseFunction(parser);
                    yield false;
                }
                case TokenKind.INDENT -> {
                    parser.bump();
                    yield false;
                }
                case TokenKind.EOF -> true;
                default -> {
                    var stmt = parser.pattern(StmtPat.instance);
                    stmts.add(stmt);

                    switch (parser.peek().kind()) {
                        case TokenKind.INDENT, TokenKind.EOF -> {
                            parser.bump();
                        }
                        default -> throw new LangError(parser.peek().pos(), "expected new statement while parsing");
                    }
                    yield false;
                }
            };

            if (term) {
                parser.expect(TokenKind.EOF, "expected end of input while parsing");
                return new Block(new Span(0, parser.src.length()), stmts);
            }
        }
    }

    // Returns null if parsed a function declaration.
    void parseFunction(Parser parser) {
        var funcPos = parser.peek().pos();
        var func = parser.expectIdent();

        var params = new ArrayList<Param>();

        parser.expect(TokenKind.PAREN_OPEN, "expected opening parenthesis while parsing");
        try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
            if (parser.peek().kind() == TokenKind.PAREN_CLOSE) {
                parser.bump();
            } else {
                while (true) {
                    var paramPos = parser.peek().pos();
                    var param = parser.expectIdent();
    
                    if (params.stream().anyMatch(p -> p.ident() == param)) {
                        var format = "parameter `%s` is already defined";
                        throw new LangError(paramPos, String.format(format, parser.ctx.symbols().resolve(param)));
                    }
    
                    var type = parseType(parser);
                    params.add(new Param(param, type));
        
                    var delim = parser.peek();
                    var term = switch (delim.kind()) {
                        case TokenKind.COMMA -> {
                            parser.bump();
                            yield false;
                        }
                        case TokenKind.PAREN_CLOSE -> {
                            parser.bump();
                            yield true;
                        }
                        default -> throw new LangError(delim.pos(), "expected closing parenthesis while parsing");
                    };
    
                    if (term) {
                        break;
                    }
                }
            }
        }

        var returnType = switch (parser.peek().kind()) {
            case TokenKind.DO -> UndefinedType.instance();
            default -> parseType(parser);
        };

        var body = parser.pattern(BlockPat.instance);

        var funcs = parser.ctx.funcs();
        if (funcs.containsKey(func)) {
            throw new LangError(funcPos, "function `" + parser.ctx.symbols().resolve(func) + "` is already defined");
        }

        funcs.put(func, new UserFunction(func, funcPos, params, returnType, body));
    }
    
    static Type parseType(Parser parser) {
        var peek = parser.peek();
        
        var type = switch (peek.kind()) {
            case TokenKind.INT -> IntType.instance();
            case TokenKind.FLOAT -> FloatType.instance();
            case TokenKind.BOOL -> BoolType.instance();
            case TokenKind.STR -> StrType.instance();
            case TokenKind.LIST -> ListType.instance();
            default -> throw new LangError(peek.pos(), "expected type while parsing");
        };
        parser.bump();

        while (true) {
            var suffix = parser.peek();
            var cons = switch (suffix.kind()) {
                case TokenKind.QMARK -> {
                    var optional = type.optional();
                    if (optional == null) {
                        throw new LangError(suffix.pos(), "type cannot be doubly nil-able");
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
