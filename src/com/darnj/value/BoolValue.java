package com.darnj.value;

import com.darnj.type.*;

public final class BoolValue extends ValueInternal {
    boolean value;

    BoolValue(boolean value) {
        super(BoolType.instance());
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof BoolValue o && value == o.value;
    }
}
