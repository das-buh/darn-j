package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.Value;

public class While extends Op {
    Op cond;
    Op body;

    public While(Span pos, Op cond, Op body) {
        super(pos);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
