package com.darnj.type;

public final class OptionalType extends Type {
    Type inner;

    public OptionalType(Type inner) {
        this.inner = inner;
    }

    @Override
    public String name() {
        return inner.name() + "?";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof NilType || other instanceof OptionalType o && inner.eq(o.inner);
    }
}
