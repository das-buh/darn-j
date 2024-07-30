package com.darnj.type;

public abstract sealed class Type
    permits IntType, FloatType, BoolType, StrType, NilType, OptionalType, ReferenceType, SliceType, UndefinedType
{
    int id;
    static int counter;

    Type() {
        id = counter;
        counter += 1;
    }

    public abstract String name();

    Type optional;
    Type reference;
    Type slice;

    public Type optional() {
        if (optional != null) {
            return optional;
        }

        optional = new OptionalType(this);
        return optional;
    }

    public Type reference() {
        if (reference != null) {
            return reference;
        }

        reference = new ReferenceType(this);
        return reference;
    }

    public Type slice() {
        if (slice != null) {
            return slice;
        }

        slice = new SliceType(this);
        return slice;
    }
}
