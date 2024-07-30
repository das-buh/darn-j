package com.darnj.type;

public final class BoolType extends Type {
    static BoolType instance = new BoolType();

    public static BoolType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "bool";
    }
}
