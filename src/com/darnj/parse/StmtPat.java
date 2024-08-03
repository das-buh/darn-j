package com.darnj.parse;

import java.util.logging.Logger;

import com.darnj.lex.*;
import com.darnj.op.*;

final class StmtPat implements Pattern<Op> {
    private static Logger log = Logger.getGlobal();

    static final StmtPat instance = new StmtPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse stmt");

        var peek = parser.peek();
        return switch (peek.kind()) {
            case TokenKind.IF -> {
                parser.bump();
                var cond = ExprPat.instance.parse(parser);
                var ifBranch = BlockPat.instance.parse(parser);

                var peekElse = parser.peekRaw();
                if (peekElse.kind() == TokenKind.ELSE && peekElse.indent() >= parser.indent) {
                    parser.bumpRaw();
                    var elseBranch = ExprPat.instance.parse(parser);
                    yield new IfElse(peek.pos().to(elseBranch.pos()), cond, ifBranch, elseBranch);
                }

                yield new IfElse(peek.pos().to(ifBranch.pos()), cond, ifBranch, null);
            }
            case TokenKind.WHILE -> {
                parser.bump();
                var cond = ExprPat.instance.parse(parser);
                var body = BlockPat.instance.parse(parser);
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
                    var value = ExprPat.instance.parse(parser);
                    yield new Return(peek.pos().to(value.pos()), value);
                }

                yield new Return(peek.pos(), null);
            }
            default -> {
                var expr = ExprPat.instance.parse(parser);

                var assign = switch (parser.peek().kind()) {
                    case TokenKind.ASSIGN -> new Assign();
                    case TokenKind.ADD_ASSIGN -> new AddAssign();
                    case TokenKind.SUB_ASSIGN -> new SubAssign();
                    case TokenKind.MUL_ASSIGN -> new MulAssign();
                    case TokenKind.DIV_ASSIGN -> new DivAssign();
                    case TokenKind.MOD_ASSIGN -> new ModAssign();
                    default -> null;
                };

                if (assign == null) {
                    yield expr;
                }

                parser.bump();
                var value = ExprPat.instance.parse(parser);
                yield assign.build(expr.pos().to(value.pos()), expr, value);
            }
        };
    }
}
