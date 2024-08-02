package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class MulAssign extends ArithmeticAssignOp {
    @Override
    Arithmetic arithmetic() {
        return ArithMul.instance;
    }
}
