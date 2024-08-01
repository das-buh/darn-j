package com.darnj.value;

import java.util.ArrayList;

import com.darnj.type.*;

public final class Value {
    public ValueInternal inner;

    public Value(ValueInternal inner) {
        this.inner = inner;
    }

    public Type type() {
        return inner.type;
    }

    public boolean eq(Value other) {
        return inner.eq(other.inner);
    }

    public Value move() {
        return new Value(inner);
    }

    public static Value makeInt(long value) {
        return new Value(new IntValue(value));
    }

    public static Value makeFloat(double value) {
        return new Value(new FloatValue(value));
    }

    public static Value makeBool(boolean value) {
        return new Value(new BoolValue(value));
    }

    public static Value makeStr(String value) {
        return new Value(new StrValue(value));
    }

    public static Value makeNil() {
        return new Value(NilValue.instance);
    }

    public static Value makeReference(Value referent) {
        return new Value(new ReferenceValue(referent));
    }

    public static Value makeList(ArrayList<Value> elems) {
        return new Value(new ListValue(elems));
    }

    public static Value makeUndefined() {
        return new Value(UndefinedValue.instance);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
