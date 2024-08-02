package com.darnj.op.arithmetic;

import com.darnj.value.*;

public final class ArithSub implements Arithmetic {
    public static ArithSub instance = new ArithSub();

    @Override
    public Value evalInt(long lhs, long rhs) {
        return Value.makeInt(lhs - rhs);
    }

    @Override
    public Value evalFloat(double lhs, double rhs) {
        return Value.makeFloat(lhs - rhs);
    }

    @Override
    public String opName() {
        return "subtract";
    }
}
