package com.darnj.parse;

import com.darnj.Error;
import com.darnj.lex.*;
import com.darnj.op.*;

public final class StmtPat extends Pattern {
    static StmtPat instance = new StmtPat();

    @Override
    Op parse(Parser parser) throws Error {
        var peek = parser.peek(0);
        return switch (peek.kind()) {
            case TokenKind.IF -> {
                parser.bump();
                var cond = parser.pattern(ExprPat.instance);
                var ifBranch = parser.pattern(BlockPat.instance);

                if (parser.peek(0).kind() == TokenKind.ELSE) {
                    parser.bump();
                    var elseBranch = parser.pattern(ExprPat.instance);
                    yield new IfElse(peek.pos().to(elseBranch.pos()), cond, ifBranch, elseBranch);
                }

                yield new IfElse(peek.pos().to(ifBranch.pos()), cond, ifBranch, null);
            }
            case TokenKind.WHILE -> {
                parser.bump();
                var cond = parser.pattern(ExprPat.instance);
                var body = parser.pattern(BlockPat.instance);
                yield new While(peek.pos().to(body.pos()), cond, body);
            }
            case TokenKind.CONTINUE -> {
                parser.bump();
                yield new Continue(peek.pos());
            }
            case TokenKind.BREAK -> {
                parser.bump();
                yield new Break(peek.pos());
            }
            case TokenKind.RETURN -> {
                parser.bump();

                if (parser.peek(0).kind() != TokenKind.INDENT) {
                    var value = parser.pattern(ExprPat.instance);
                    yield new Return(peek.pos().to(value.pos()), value);
                }

                yield new Return(peek.pos(), null);
            }
            default -> {
                var expr = parser.pattern(ExprPat.instance);

                if (parser.peek(0).kind() == TokenKind.ASSIGN) {
                    parser.bump();
                    var assign = parser.pattern(ExprPat.instance);
                    
                    if (!opIsAssignable(expr)) {
                        throw new Error(expr.pos(), "invalid assignee");
                    }
                    yield new Assign(expr.pos().to(assign.pos()), expr, assign);
                }

                yield expr;
            }
        };
    }

    static boolean opIsAssignable(Op op) {
        return switch (op) {
            case Variable _ -> true;
            case Deref ref -> opIsAssignable(ref);
            default -> false;
        };
    }
}
