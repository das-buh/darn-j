package com.darnj.type;

public final class StrType extends Type {
    static StrType instance = new StrType();

    public static StrType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "str";
    }
}
