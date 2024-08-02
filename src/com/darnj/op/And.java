package com.darnj.op;

public final class And extends ShortCircuitOp {
    @Override
    boolean shortValue() {
        return false;
    }

    @Override
    String opName() {
        return "logical-and";
    }
}
