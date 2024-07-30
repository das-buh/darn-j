package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Continue extends Op {
    public Continue(Span pos) {
        super(pos);
    }

    @Override
    public Value eval(Context ctx) throws Error {
        
    }
}