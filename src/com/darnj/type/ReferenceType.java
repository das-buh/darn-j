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
        return other instanceof ReferenceType a && inner.eq(a.inner)
            || other instanceof OptionalType b && b.inner instanceof ReferenceType c && inner.eq(c.inner);
    }
}
