package com.darnj.value;

import com.darnj.type.*;

public final class NilValue extends ValueInternal {
    static NilValue instance = new NilValue();

    NilValue() {
        super(NilType.instance());
    } 

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof NilValue;
    }
}
