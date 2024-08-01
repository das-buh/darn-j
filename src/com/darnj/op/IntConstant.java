package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class IntConstant extends Op {
    long val;

    public IntConstant(Span pos, long val) {
        this.pos = pos;
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) {
        return Value.makeInt(val);
    }
}

