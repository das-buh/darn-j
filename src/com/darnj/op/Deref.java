package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Deref extends UnaryOp implements Assignable {
    @Override
    public Value eval(Context ctx) {
        return dereference(ctx).move();
    }

    @Override
    public Value referent(Context ctx) {
        return dereference(ctx);
    }

    Value dereference(Context ctx) {
        var operand = this.operand.eval(ctx);
        if (operand.inner instanceof ReferenceValue o) {
            return o.referent();
        }

        throw error(String.format("cannot dereference type %s", operand.type().name()));
    }
}
