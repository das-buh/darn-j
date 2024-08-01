package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Return extends Op {
    // Null if undefined return value.
    Op value;
    
    public Return(Span pos, Op value) {
        this.pos = pos;
        this.value = value;
    }

    @Override
    public Value eval(Context ctx) {
        var value = this.value != null ? this.value.eval(ctx) : Value.makeUndefined(); 
        throw new ReturnEffect(pos, value);
    }
}