package com.darnj.parse;

import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.lex.*;
import com.darnj.op.*;

///////////////////////////////////////
//      (greatest precedence)
//      Function calls
//      Unary - * &
//      * / %
//      + -
//      == != < > <= >=
//      not
//      and
//      or
//      (least precedence)
///////////////////////////////////////

final class ExprPat implements Pattern {
    private static Logger log = Logger.getGlobal();

    static ExprPat instance = new ExprPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse expr");

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

                throw new LangError(peekElse.pos(), "expected else branch while parsing");
            }
            default -> parser.pattern(OrPat.instance);
        };
    }
}

abstract class InfixPat implements Pattern {
    abstract Op operand(Parser parser);
    
    // Returns null if no operator is matched.
    abstract BinaryOp match(TokenKind op);

    @Override
    public Op parse(Parser parser) {
        var op = operand(parser);

        while (true) {
            var cons = match(parser.peek().kind());

            if (cons == null) {
                return op;
            }

            parser.bump();
            var rhs = operand(parser);
            op = new BinaryOpBuilder(cons).build(op.pos().to(rhs.pos()), op, rhs);
        }
    }
}

abstract class PrefixPat implements Pattern {
    abstract Op operand(Parser parser);
    
    // Returns null if no operator is matched.
    abstract UnaryOp match(TokenKind op);

    @Override
    public Op parse(Parser parser) {
        var op = match(parser.peek().kind());

        if (op == null) {
            return operand(parser);
        }

        parser.bump();
        var operand = parse(parser);
        return new UnaryOpBuilder(op).build(op.pos().to(operand.pos()), operand);
    }
}

final class OrPat extends InfixPat {
    static OrPat instance = new OrPat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(AndPat.instance);
    }

    @Override
    BinaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.OR -> new Or();
            default -> null;
        };
    }
}

final class AndPat extends InfixPat {
    static AndPat instance = new AndPat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(NotPat.instance);
    }

    @Override
    BinaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.AND -> new And();
            default -> null;
        };
    }
}

final class NotPat extends PrefixPat {
    static NotPat instance = new NotPat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(ComparePat.instance);
    }

    @Override
    UnaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.NOT -> new Not();
            default -> null;
        };
    }
}

final class ComparePat extends InfixPat {
    static ComparePat instance = new ComparePat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(SumPat.instance);
    }

    @Override
    BinaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.EQ -> new Eq();
            case TokenKind.NEQ -> new Neq();
            case TokenKind.LT -> new Lt();
            case TokenKind.LTE -> new Lte();
            case TokenKind.GT -> new Gt();
            case TokenKind.GTE -> new Gte();
            default -> null;
        };
    }
}

final class SumPat extends InfixPat {
    static SumPat instance = new SumPat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(ProductPat.instance);
    }

    @Override
    BinaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.PLUS -> new Add();
            case TokenKind.MINUS -> new Sub();
            default -> null;
        };
    }
}

final class ProductPat extends InfixPat {
    static ProductPat instance = new ProductPat();
    
    @Override
    Op operand(Parser parser) {
        return parser.pattern(MiscPrefixPat.instance);
    }

    @Override
    BinaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.STAR -> new Mul();
            case TokenKind.SLASH -> new Div();
            case TokenKind.MODULO -> new Mod();
            default -> null;
        };
    }
}

final class MiscPrefixPat extends PrefixPat {
    static MiscPrefixPat instance = new MiscPrefixPat();

    @Override
    Op operand(Parser parser) {
        return parser.pattern(AtomPat.instance);
    }

    @Override
    UnaryOp match(TokenKind op) {
        return switch (op) {
            case TokenKind.MINUS -> new Neg();
            case TokenKind.STAR -> new Deref();
            case TokenKind.REF -> new Ref();
            default -> null;
        };
    }
}
