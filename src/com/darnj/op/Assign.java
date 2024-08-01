package com.darnj.op;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Assign extends Op {
    Op assignee;
    Op value;

    public Assign(Span pos, Op assignee, Op value) {
        this.pos = pos;
        this.assignee = assignee;
        this.value = value;
    }
    
    @Override
    public Value eval(Context ctx) {
        if (assignee instanceof Assignable a) {
            var referent = a.referent(ctx);
            referent.inner = value.eval(ctx).inner;
            return Value.makeUndefined();
        }
        
        throw new LangError(assignee.pos(), "invalid assignee");
    }
}
