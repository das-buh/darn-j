package com.darnj.type;

public final class UndefinedType extends Type {
    static UndefinedType instance = new UndefinedType();

    public static UndefinedType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "undefined";
    }
}
