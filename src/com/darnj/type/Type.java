package com.darnj.type;

public abstract sealed class Type
    permits IntType, FloatType, BoolType, StrType, NilType, OptionalType, ReferenceType, ListType, UndefinedType
{
    public abstract String name();

    public abstract boolean eq(Type other);

    Type optional;
    Type reference;

    // Returns null if already optional.
    public Type optional() {
        if (optional != null) {
            return optional;
        }

        if (optional instanceof OptionalType) {
            return null;
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
}
