package com.darnj.op;

import com.darnj.op.arithmetic.*;

public final class Mod extends ArithmeticOp {
    @Override
    Arithmetic arithmetic() {
        return ArithMod.instance;
    }
}
