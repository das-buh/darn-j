package com.darnj.type;

public final class ReferenceType extends Type {
    Type inner;

    public ReferenceType(Type inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return inner.name() + "*";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof ReferenceType o && inner.eq(o.inner);
    }
}
