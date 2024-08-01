package com.darnj.value;

import com.darnj.type.*;

public final class UndefinedValue extends ValueInternal {
    static UndefinedValue instance = new UndefinedValue();

    UndefinedValue() {
        super(UndefinedType.instance());
    }

    @Override
    boolean eq(ValueInternal other) {
        throw new RuntimeException("tried to eq undefined");
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
