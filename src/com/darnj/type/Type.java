package com.darnj.type;

import java.util.Optional;

public abstract sealed class Type
    permits IntType, FloatType, BoolType, StrType, NilType, OptionalType, ReferenceType, ListType, UndefinedType
{
    public abstract String name();

    public abstract boolean eq(Type other);

    Optional<Type> optional;
    Type reference;

    // Returns Optional.empty if already optional.
    public Optional<Type> optional() {
        if (optional != null) {
            return optional;
        }

        if (this instanceof OptionalType) {
            optional = Optional.empty();
            return optional;
        }

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
