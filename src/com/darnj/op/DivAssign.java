package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class DivAssign extends ArithmeticAssignOp {
    @Override
    Arithmetic arithmetic() {
        return ArithDiv.instance;
    }
}
