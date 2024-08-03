package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.op.arithmetic.*;
import com.darnj.value.*;

public abstract class ArithmeticOp extends BinaryOp {
    abstract Arithmetic arithmetic();

    @Override
    public Value eval(Context ctx) {
        var arithmetic = arithmetic();
        var lhs = this.lhs.eval(ctx);
        var rhs = this.rhs.eval(ctx);
        
        if (lhs.inner instanceof IntValue l && rhs.inner instanceof IntValue r) {
            return arithmetic.evalInt(l.value(), r.value());
        } else if (lhs.inner instanceof FloatValue l && rhs.inner instanceof FloatValue r) {
            return arithmetic.evalFloat(l.value(), r.value());
        } else {
            throw error(
                "cannot %s types %s and %s", 
                arithmetic.opName(),
                lhs.type().name(), 
                rhs.type().name()
            );
        }
    }
}
