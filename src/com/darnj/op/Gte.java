package com.darnj.op;

import com.darnj.type.*;
import com.darnj.value.*;

public final class Gte extends NumBinOp {
    @Override
    Value evalInt(long lhs, long rhs) {
        return Value.makeBool(lhs >= rhs);
    }

    @Override
    Value evalFloat(double lhs, double rhs) {
        return Value.makeBool(lhs >= rhs);
    }

    @Override
    String typeMismatch(Type lhs, Type rhs) {
        return String.format("cannot compare types %s and %s", lhs.name(), rhs.name());
    }
}
