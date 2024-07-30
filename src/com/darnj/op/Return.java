package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Return extends Op {
    // Null if nil return value.
    Op value;
    
    public Return(Span pos, Op value) {
        super(pos);
        this.value = value;
    }

    @Override
    public Value eval(Context ctx) throws Error {
        
    }
}