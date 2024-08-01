package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class True extends Op {
    public True(Span pos) {
        this.pos = pos;
    }
    
    @Override
    public Value eval(Context ctx) {
        return Value.makeBool(true);
    }
}
