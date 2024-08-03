package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Deref extends UnaryOp {
    @Override
    public Value eval(Context ctx) {
        return dereference(ctx).move();
    }

    public Value dereference(Context ctx) {
        var operand = this.operand.eval(ctx);
        if (operand.inner instanceof ReferenceValue o) {
            return o.referent();
        }

        throw error("cannot dereference type %s", operand.type().name());
    }
}
