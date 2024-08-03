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
            case Token.Kind.IF -> {
                parser.bump();
                var cond = ExprPat.instance.parse(parser);
                var ifBranch = BlockPat.instance.parse(parser);

                var peekElse = parser.peekRaw();
                if (peekElse.kind() == Token.Kind.ELSE && peekElse.indent() >= parser.indent) {
                    parser.bumpRaw();
                    var elseBranch = ExprPat.instance.parse(parser);
                    yield new IfElse(peek.pos().to(elseBranch.pos()), cond, ifBranch, elseBranch);
                }

                yield new IfElse(peek.pos().to(ifBranch.pos()), cond, ifBranch);
            }
            case Token.Kind.WHILE -> {
                parser.bump();
                var cond = ExprPat.instance.parse(parser);
                var body = BlockPat.instance.parse(parser);
                yield new While(peek.pos().to(body.pos()), cond, body);
            }
            case Token.Kind.CONTINUE -> {
                parser.bump();
                yield new Continue(peek.pos());
            }
            case Token.Kind.BREAK -> {
                parser.bump();
                yield new Break(peek.pos());
            }
            case Token.Kind.RETURN -> {
                parser.bump();

                if (!parser.peek().isDelim()) {
                    var value = ExprPat.instance.parse(parser);
                    yield new Return(peek.pos().to(value.pos()), value);
                }

                yield new Return(peek.pos());
            }
            default -> {
                var expr = ExprPat.instance.parse(parser);

                var assign = switch (parser.peek().kind()) {
                    case Token.Kind.ASSIGN -> new Assign();
                    case Token.Kind.ADD_ASSIGN -> new AddAssign();
                    case Token.Kind.SUB_ASSIGN -> new SubAssign();
                    case Token.Kind.MUL_ASSIGN -> new MulAssign();
                    case Token.Kind.DIV_ASSIGN -> new DivAssign();
                    case Token.Kind.MOD_ASSIGN -> new ModAssign();
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
