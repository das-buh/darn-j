package com.darnj.value;

import com.darnj.type.*;

abstract class ValueInternal {
    Type type;

    ValueInternal(Type type) {
        this.type = type;
    }

    abstract boolean eq(ValueInternal other);
}
