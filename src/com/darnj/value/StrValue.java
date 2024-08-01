package com.darnj.value;

import com.darnj.type.*;

public final class StrValue extends ValueInternal {
    String value;

    StrValue(String value) {
        super(StrType.instance());
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof StrValue o && value.equals(o.value);
    }
}
