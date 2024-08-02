package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.op.arithmetic.*;
import com.darnj.value.*;

public final class Gte extends ArithmeticOp {
    @Override
    public Value eval(Context ctx) {
        return evalArithmetic(ctx, ArithGte.instance);
    }
}
