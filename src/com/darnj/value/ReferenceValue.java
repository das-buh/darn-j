package com.darnj.value;

public final class ReferenceValue extends ValueInternal {
    Value referent;

    ReferenceValue(Value referent) {
        super(referent.inner.type.reference());
        this.referent = referent;
    }

    public Value inner() {
        return referent;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof ReferenceValue o && referent.inner.eq(o.referent.inner);
    }
}
