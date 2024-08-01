package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Identity extends Op {
    Op operand;

    public Identity(Span pos, Op operand) {
        this.pos = pos;
        this.operand = operand;
    }

    @Override
    public Value eval(Context ctx) {
        return operand.eval(ctx);
    }
}
