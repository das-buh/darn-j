package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Div extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithDiv.instance;
    }
}
