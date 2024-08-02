package com.darnj.op;

public final class Neq extends EqualityOp {
    @Override
    boolean transform(boolean bool) {
        return !bool;
    }
}