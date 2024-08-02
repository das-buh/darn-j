package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Lt extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithLt.instance;
    }
}
