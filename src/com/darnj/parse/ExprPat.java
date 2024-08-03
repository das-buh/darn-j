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

final class ExprPat implements Pattern<Op> {
    private static Logger log = Logger.getGlobal();

    static final ExprPat instance = new ExprPat();

    @Override
    public Op parse(Parser parser) {
        log.finer("parse expr");

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

                throw new LangError(peekElse.pos(), "expected else branch while parsing");
            }
            case TokenKind.DO -> BlockPat.instance.parse(parser);
            default -> OrPat.instance.parse(parser);
        };
    }
}

abstract class InfixPat implements Pattern<Op> {
    abstract Pattern<Op> operand();
    
    // TODO check if nullable allowed
    // Returns null if no operator is matched.
    abstract BinaryOp match(TokenKind op);

    @Override
    public Op parse(Parser parser) {
        var operand = operand();
        var op = operand.parse(parser);

        while (true) {
            var cons = match(parser.peek().kind());

            if (cons == null) {
                return op;
            }

            parser.bump();
            var rhs = operand.parse(parser);
            op = cons.build(op.pos().to(rhs.pos()), op, rhs);
        }
    }
}

abstract class PrefixPat implements Pattern<Op> {
    abstract Pattern<Op> fallback();
    
    // Returns null if no operator is matched.
    abstract UnaryOp match(TokenKind op);

    @Override
    public Op parse(Parser parser) {
        var fallback = fallback();
        var prefix = parser.peek();
        var op = match(prefix.kind());

        if (op == null) {
            return fallback.parse(parser);
        }

        parser.bump();
        var operand = parse(parser);
        return op.build(prefix.pos().to(operand.pos()), operand);
    }
}

final class OrPat extends InfixPat {
    static final OrPat instance = new OrPat();

    @Override
    Pattern<Op> operand() {
        return AndPat.instance;
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
    static final AndPat instance = new AndPat();

    @Override
    Pattern<Op> operand() {
        return NotPat.instance;
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
    static final NotPat instance = new NotPat();

    @Override
    Pattern<Op> fallback() {
        return ComparePat.instance;
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
    static final ComparePat instance = new ComparePat();

    @Override
    Pattern<Op> operand() {
        return SumPat.instance;
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
    static final SumPat instance = new SumPat();

    @Override
    Pattern<Op> operand() {
        return ProductPat.instance;
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
    static final ProductPat instance = new ProductPat();
    
    @Override
    Pattern<Op> operand() {
        return MiscPrefixPat.instance;
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
    static final MiscPrefixPat instance = new MiscPrefixPat();

    @Override
    Pattern<Op> fallback() {
        return AtomPat.instance;
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
