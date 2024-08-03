package com.darnj.type;

import java.util.Optional;

public abstract sealed class Type
        permits IntType, FloatType, BoolType, StrType, NilType, OptionalType, ReferenceType, ListType, UndefinedType {
    public abstract String name();

    public abstract boolean eq(Type other);

    private boolean optionalIsInit;
    private Optional<Type> optional;
    private Type reference;

    // Returns Optional.empty if already optional.
    public Optional<Type> optional() {
        if (optionalIsInit) {
            return optional;
        }

        if (this instanceof OptionalType) {
            optionalIsInit = true;
            optional = Optional.empty();
            return optional;
        }

        optionalIsInit = true;
        optional = Optional.of(new OptionalType(this));
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
