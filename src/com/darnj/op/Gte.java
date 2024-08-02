package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Gte extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithGte.instance;
    }
}
