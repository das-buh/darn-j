package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class FloatConstant extends Op {
    double val;

    public FloatConstant(Span pos, double val) {
        this.pos = pos;
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) {
        return Value.makeFloat(val);
    }
}
