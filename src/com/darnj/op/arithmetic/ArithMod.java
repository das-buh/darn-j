package com.darnj.op.arithmetic;

import com.darnj.value.*;

public final class ArithMod implements Arithmetic {
    public static ArithMod instance = new ArithMod();

    @Override
    public Value evalInt(long lhs, long rhs) {
        return Value.makeInt(lhs % rhs);
    }

    @Override
    public Value evalFloat(double lhs, double rhs) {
        return Value.makeFloat(lhs % rhs);
    }

    @Override
    public String opName() {
        return "modulo";
    }
}
