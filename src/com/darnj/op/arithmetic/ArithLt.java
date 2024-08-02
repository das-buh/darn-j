package com.darnj.op.arithmetic;

import com.darnj.value.*;

public final class ArithLt implements Arithmetic {
    public static ArithLt instance = new ArithLt();

    @Override
    public Value evalInt(long lhs, long rhs) {
        return Value.makeBool(lhs < rhs);
    }

    @Override
    public Value evalFloat(double lhs, double rhs) {
        return Value.makeBool(lhs < rhs);
    }

    @Override
    public String opName() {
        return "compare";
    }
}
