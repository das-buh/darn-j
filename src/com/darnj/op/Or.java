package com.darnj.op;

public final class Or extends ShortCircuitOp {
    @Override
    boolean shortValue() {
        return true;
    }

    @Override
    String opName() {
        return "logical-or";
    }
}
