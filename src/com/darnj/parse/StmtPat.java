package com.darnj.parse;

import com.darnj.lex.*;
import com.darnj.op.*;

final class StmtPat implements Pattern {
    static StmtPat instance = new StmtPat();

    @Override
    public Op parse(Parser parser) {
        var peek = parser.peek();
        return switch (peek.kind()) {
            case TokenKind.IF -> {
                parser.bump();
                var cond = parser.pattern(ExprPat.instance);
                var ifBranch = parser.pattern(BlockPat.instance);

                var peekElse = parser.peekRaw();
                if (peekElse.kind() == TokenKind.ELSE && peekElse.indent() == parser.indent) {
                    parser.bumpRaw();
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

                if (parser.peek().kind() != TokenKind.INDENT) {
                    var value = parser.pattern(ExprPat.instance);
                    yield new Return(peek.pos().to(value.pos()), value);
                }

                yield new Return(peek.pos(), null);
            }
            default -> {
                var expr = parser.pattern(ExprPat.instance);

                if (parser.peek().kind() == TokenKind.ASSIGN) {
                    parser.bump();
                    var assign = parser.pattern(ExprPat.instance);
                    yield new Assign(expr.pos().to(assign.pos()), expr, assign);
                }

                yield expr;
            }
        };
    }
}
