package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Nil extends Op {
    public Nil(Span pos) {
        super(pos);
    }
    
    @Override
    public Value eval(Context ctx) throws Error {

    }
}
