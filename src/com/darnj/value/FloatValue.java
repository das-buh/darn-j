package com.darnj.value;

import com.darnj.type.*;

public final class FloatValue extends ValueInternal {
    double value;

    FloatValue(double value) {
        super(FloatType.instance());
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof FloatValue o && value == o.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
