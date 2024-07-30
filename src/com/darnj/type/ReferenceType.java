package com.darnj.type;

public final class ReferenceType extends Type {
    Type inner;

    public ReferenceType(Type inner) {
        super();
        this.inner = inner;
    }

    @Override
    public String name() {
        return inner.name() + "*";
    }
}
