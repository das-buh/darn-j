package com.darnj.type;

public final class SliceType extends Type {
    Type inner;

    public SliceType(Type inner) {
        super();
        this.inner = inner;
    }

    @Override
    public String name() {
        return "[" + inner.name() + "]";
    }
}
