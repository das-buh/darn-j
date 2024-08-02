package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class AddAssign extends ArithmeticAssignOp {
    @Override
    Arithmetic arithmetic() {
        return ArithAdd.instance;
    }
}
