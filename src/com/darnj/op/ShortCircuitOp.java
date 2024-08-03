package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public abstract class ShortCircuitOp extends BinaryOp {
    abstract boolean shortValue();
    abstract String opName();

    @Override
    public Value eval(Context ctx) {
        var lhs = this.lhs.eval(ctx);
        if (lhs.inner instanceof BoolValue l) {
            if (l.value() == shortValue()) {
                return Value.makeBool(shortValue());
            }

            var rhs = this.rhs.eval(ctx);
            if (rhs.inner instanceof BoolValue r) {
                return Value.makeBool(r.value());
            }
            
            throw error("cannot %s types %s and %s", opName(), lhs.type().name(), rhs.type().name());
        }

        throw error("cannot %s type %s", opName(), lhs.type().name());
    }
}
