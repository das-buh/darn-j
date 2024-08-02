package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public abstract class EqualityOp extends BinaryOp {
    abstract boolean transform(boolean bool);

    @Override
    public Value eval(Context ctx) {
        var lhs = this.lhs.eval(ctx);
        var rhs = this.rhs.eval(ctx);

        var undefined = lhs.inner instanceof UndefinedValue || rhs.inner instanceof UndefinedValue;
        var comparable = lhs.inner instanceof NilValue || rhs.inner instanceof NilValue || lhs.type() == rhs.type();
        if (!undefined && comparable) {
            return Value.makeBool(transform(lhs.eq(rhs)));
        }
        
        var format = "cannot check equality of types %s and %s";
        throw error(String.format(format, lhs.type().name(), rhs.type().name()));
    }
}