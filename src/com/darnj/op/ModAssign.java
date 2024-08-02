package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class ModAssign extends ArithmeticAssignOp {
    @Override
    Arithmetic arithmetic() {
        return ArithMod.instance;
    }
}
