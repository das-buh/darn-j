package com.darnj.value;

import com.darnj.type.*;

public final class IntValue extends ValueInternal {
    long value;

    IntValue(long value) {
        super(IntType.instance());
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof IntValue o && value == o.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
