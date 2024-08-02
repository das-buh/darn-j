package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Gt extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithGt.instance;
    }
}
