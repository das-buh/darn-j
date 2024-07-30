package com.darnj.type;

public final class OptionalType extends Type {
    Type inner;

    public OptionalType(Type inner) {
        super();
        this.inner = inner;
    }

    @Override
    public String name() {
        return inner.name() + "?";
    }
}
