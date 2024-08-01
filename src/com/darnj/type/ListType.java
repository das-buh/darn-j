package com.darnj.type;

public final class ListType extends Type {
    static ListType instance = new ListType();

    public static ListType instance() {
        return instance;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public boolean eq(Type other) {
        return other instanceof ListType;
    }
}
