package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Add extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithAdd.instance;
    }
}
