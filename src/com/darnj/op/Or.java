package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Or extends BinaryOp {
    @Override
    public Value eval(Context ctx) {
        var lhs = this.lhs.eval(ctx);
        if (lhs.inner instanceof BoolValue l) {
            if (l.value()) {
                return Value.makeBool(true);
            }

            var rhs = this.rhs.eval(ctx);
            if (rhs.inner instanceof BoolValue r) {
                return Value.makeBool(l.value() | r.value());
            }
            
            throw error(String.format("cannot logical-or types %s and %s", lhs.type().name(), rhs.type().name()));
        }

        throw error(String.format("cannot logical-or type %s", lhs.type().name()));
    }
}
