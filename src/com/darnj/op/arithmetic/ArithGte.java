package com.darnj.op.arithmetic;

import com.darnj.value.*;

public final class ArithGte implements Arithmetic {
    public static ArithGte instance = new ArithGte();

    @Override
    public Value evalInt(long lhs, long rhs) {
        return Value.makeBool(lhs >= rhs);
    }

    @Override
    public Value evalFloat(double lhs, double rhs) {
        return Value.makeBool(lhs >= rhs);
    }

    @Override
    public String opName() {
        return "compare";
    }
}
