package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public abstract class Op {
    Span pos;

    Op(Span pos) {
        this.pos = pos;
    }

    public Span pos() {
        return pos;
    }

    public abstract Value eval(Context ctx) throws Error;
}
