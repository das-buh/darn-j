package com.darnj.op.arithmetic;

import com.darnj.value.*;

public final class ArithDiv implements Arithmetic {
    public static ArithDiv instance = new ArithDiv();

    @Override
    public Value evalInt(long lhs, long rhs) {
        return Value.makeInt(lhs / rhs);
    }

    @Override
    public Value evalFloat(double lhs, double rhs) {
        return Value.makeFloat(lhs / rhs);
    }

    @Override
    public String opName() {
        return "divide";
    }
}
