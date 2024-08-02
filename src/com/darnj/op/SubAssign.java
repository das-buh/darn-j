package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class SubAssign extends ArithmeticAssignOp {
    @Override
    Arithmetic arithmetic() {
        return ArithSub.instance;
    }
}
