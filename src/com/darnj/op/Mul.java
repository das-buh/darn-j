package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Mul extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithMul.instance;
    }
}
