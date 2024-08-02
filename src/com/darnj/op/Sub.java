package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Sub extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithSub.instance;
    }
}
