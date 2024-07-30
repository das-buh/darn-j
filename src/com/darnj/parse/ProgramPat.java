package com.darnj.parse;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.interpret.*;
import com.darnj.lex.*;
import com.darnj.op.*;
import com.darnj.type.*;

public final class ProgramPat extends Pattern {
    static ProgramPat instance = new ProgramPat();

    @Override
    Op parse(Parser parser) throws Error {
        var peek = parser.peek(0);
        switch (peek.kind()) {
            case TokenKind.FN -> {
                parser.bump();
                parseFunction(parser);
            }
            default -> parser.pattern(StmtPat.instance);
        };
        // TODO
    }

    // Returns null if parsed a function declaration.
    Op parseFunction(Parser parser) throws Error {
        var func = parser.expectIdent();

        parser.expect(TokenKind.PAREN_OPEN, "expected opening parenthesis while parsing");
        return parser.withIndentSensitivity(false, new Pattern() {
            @Override
            Op parse(Parser parser) throws Error {
                var params = new ArrayList<IdentWithType>();
                if (parser.peek(0).kind() == TokenKind.PAREN_CLOSE) {
                    parser.bump();
                } else while (true) {
                    var param = parser.expectIdent();
                    var type = parseType(parser);
                    params.add(new IdentWithType(param, type));
        
                    var delim = parser.peek(0);
                    switch (delim.kind()) {
                        case TokenKind.COMMA -> {
                            parser.bump();
                            continue;
                        }
                        case TokenKind.PAREN_CLOSE -> {
                            parser.bump();
                            break;
                        }
                        default -> throw new Error(delim.pos(), "expected closing parenthesis while parsing");
                    }
                };

                var returnType = switch (parser.peek(0).kind()) {
                    case TokenKind.DO -> {
                        parser.bump();
                        yield NilType.instance();
                    }
                    default -> parseType(parser);
                };

                var body = parser.pattern(BlockPat.instance);

                // TODO add to context
                new Function(func, params, returnType, body);
                return null;
            }
        });
    }
    
    static Type parseType(Parser parser) throws Error {
        var peek = parser.peek(0);
        
        var type = switch (peek.kind()) {
            case TokenKind.BRACKET_OPEN -> {
                parser.bump();
                var elem = parseType(parser);
                parser.expect(TokenKind.BRACKET_CLOSE, "expected closing bracket while parsing");
                yield elem.slice();
            }
            default -> {
                var prim = switch (peek.kind()) {
                    case TokenKind.INT -> IntType.instance();
                    case TokenKind.FLOAT -> FloatType.instance();
                    case TokenKind.BOOL -> BoolType.instance();
                    case TokenKind.STR -> StrType.instance();
                    default -> throw new Error(peek.pos(), "expected type while parsing");
                };
                parser.bump();
                yield prim;
            }
        };

        while (true) {
            var cons = switch (peek.kind()) {
                case TokenKind.QMARK -> type.optional();
                case TokenKind.STAR -> type.reference();
                default -> null;
            };

            if (cons == null) {
                return type;
            }

            type = cons;
        }
    }
}
