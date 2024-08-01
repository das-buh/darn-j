package com.darnj.type;

public final class IntType extends Type {
    static IntType instance = new IntType();

    public static IntType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "int";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof IntType || other instanceof OptionalType o && o.inner instanceof IntType;
    }
}
