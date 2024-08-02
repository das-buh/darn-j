package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.op.arithmetic.*;
import com.darnj.value.*;

public final class Div extends ArithmeticOp {
    @Override
    public Value eval(Context ctx) {
        return evalArithmetic(ctx, ArithDiv.instance);
    }
}
