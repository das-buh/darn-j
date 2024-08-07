package com.darnj.type;

public final class NilType extends Type {
    static NilType instance = new NilType();

    public static NilType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "nil";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof NilType || other instanceof OptionalType;
    }
}
