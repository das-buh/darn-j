package com.darnj.type;

public final class FloatType extends Type {
    static FloatType instance = new FloatType();

    public static FloatType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "float";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof FloatType;
    }
}
