package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class FloatConstant extends Op {
    double val;

    public FloatConstant(Span pos, double val) {
        super(pos);
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
