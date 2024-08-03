package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Not extends UnaryOp {
    @Override
    public Value eval(Context ctx) {
        var operand = this.operand.eval(ctx);
        if (operand.inner instanceof BoolValue o) {
            return Value.makeBool(!o.value());
        }
        
        throw error("cannot logical-not type %s", operand.type().name());
    }
}
