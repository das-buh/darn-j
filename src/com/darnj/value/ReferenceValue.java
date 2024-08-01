package com.darnj.value;

public final class ReferenceValue extends ValueInternal {
    Value referent;

    ReferenceValue(Value referent) {
        super(referent.inner.type.reference());
        this.referent = referent;
    }

    public Value referent() {
        return referent;
    }

    @Override
    boolean eq(ValueInternal other) {
        return other instanceof ReferenceValue o && referent.inner.eq(o.referent.inner);
    }

    @Override
    public String toString() {
        return referent.toString();
    }
}
