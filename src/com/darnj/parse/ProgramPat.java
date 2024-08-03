package com.darnj.parse;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;
import com.darnj.type.*;

final class ProgramPat implements Pattern<Op> {
    private static Logger log = Logger.getGlobal();

    static final ProgramPat instance = new ProgramPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse program");
        
        var stmts = new ArrayList<Op>();

        while (true) {
            if (parser.peek().kind() == TokenKind.EOF) {
                parser.expect(TokenKind.EOF, "expected end of input while parsing");
                return new Block(new Span(0, parser.src.length()), stmts);
            }

            switch (parser.peek().kind()) {
                case TokenKind.FN -> {
                    parser.bump();
                    parseFunction(parser);
                }
                case TokenKind.INDENT -> {
                    parser.bump();
                }
                default -> {
                    var stmt = StmtPat.instance.parse(parser);
                    stmts.add(stmt);

                    if (!parser.peek().isDelim()) {
                        throw new LangError(parser.peek().pos(), "expected new statement while parsing");
                    }
                }
            }
        }
    }

    private void parseFunction(Parser parser) {
        var funcPos = parser.peek().pos();
        var funcName = parser.expectIdent();
        
        ArrayList<Param> params;
        parser.expect(TokenKind.PAREN_OPEN, "expected opening parenthesis while parsing");
        try (var indentHandler = parser.new IndentSensitivityHandler(false)) {
            params = switch (parser.peek().kind()) {
                case TokenKind.PAREN_CLOSE -> {
                    parser.bump();
                    yield new ArrayList<>();
                }
                default -> {
                    var paramsList = parser.commaSeparated(p -> {
                        var paramPos = p.peek().pos();
                        var paramName = p.expectIdent();
                        var paramType = parseType(p);
                        return new Param(paramPos, paramName, paramType);
                    });
                    parser.expect(TokenKind.PAREN_CLOSE, "expected closing parenthesis while parsing");
                    yield paramsList;
                }
            };
        }

        var paramCount = params.size();
        for (var i = 0; i < paramCount; i++) {
            var param = params.get(i);
            for (var j = 0; j < i; j++) {
                if (params.get(j).ident() == param.ident()) {
                    var format = "parameter `%s` is already defined";
                    throw new LangError(param.pos(), String.format(format, parser.ctx.symbols().resolve(param.ident())));
                }
            }
        }

        var returnType = switch (parser.peek().kind()) {
            case TokenKind.DO -> UndefinedType.instance();
            default -> parseType(parser);
        };

        var body = BlockPat.instance.parse(parser);

        var funcs = parser.ctx.funcs();
        if (funcs.containsKey(funcName)) {
            throw new LangError(funcPos, "function `" + parser.ctx.symbols().resolve(funcName) + "` is already defined");
        }

        funcs.put(funcName, new UserFunction(funcName, funcPos, params, returnType, body));
    }
    
    private Type parseType(Parser parser) {
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
