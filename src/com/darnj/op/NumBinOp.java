package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.type.*;
import com.darnj.value.*;

public abstract class NumBinOp extends BinaryOp {
    abstract Value evalInt(long lhs, long rhs);
    abstract Value evalFloat(double lhs, double rhs);
    abstract String typeMismatch(Type lhs, Type rhs);

    @Override
    public Value eval(Context ctx) {
        var lhs = this.lhs.eval(ctx);
        var rhs = this.rhs.eval(ctx);
        
        if (lhs.inner instanceof IntValue l && rhs.inner instanceof IntValue r) {
            return evalInt(l.value(), r.value());
        } else if (lhs.inner instanceof FloatValue l && rhs.inner instanceof FloatValue r) {
            return evalFloat(l.value(), r.value());
        } else {
            throw error(typeMismatch(lhs.type(), rhs.type()));
        }
    }
}
