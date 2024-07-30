package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Variable extends Op {
    int id;

    public Variable(Span pos, int id) {
        super(pos);
        this.id = id;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
