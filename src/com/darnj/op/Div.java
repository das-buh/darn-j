package com.darnj.op;

import com.darnj.type.*;
import com.darnj.value.*;

public final class Div extends NumBinOp {
    @Override
    Value evalInt(long lhs, long rhs) {
        return Value.makeInt(lhs / rhs);
    }

    @Override
    Value evalFloat(double lhs, double rhs) {
        return Value.makeFloat(lhs / rhs);
    }

    @Override
    String typeMismatch(Type lhs, Type rhs) {
        return String.format("cannot divide types %s and %s", lhs.name(), rhs.name());
    }
}
