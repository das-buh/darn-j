package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class IntConstant extends Op {
    long val;

    public IntConstant(Span pos, long val) {
        super(pos);
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
