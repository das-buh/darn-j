package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Lte extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithLte.instance;
    }
}
