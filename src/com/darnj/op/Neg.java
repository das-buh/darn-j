package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Neg extends UnaryOp {
    @Override
    public Value eval(Context ctx) {
        var operand = this.operand.eval(ctx);
        return switch (operand.inner) {
            case IntValue o -> Value.makeInt(-o.value());
            case FloatValue o -> Value.makeFloat(-o.value());
            default -> throw error(String.format("cannot negate type %s", operand.type().name()));
        };
        
    }
}
