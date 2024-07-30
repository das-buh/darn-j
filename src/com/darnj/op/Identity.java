package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Identity extends Op {
    Op operand;

    public Identity(Span pos, Op operand) {
        super(pos);
        this.operand = operand;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
