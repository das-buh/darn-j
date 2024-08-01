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
        switch (assignee) {
            case Variable a -> {
                var value = this.value.eval(ctx).inner;

                if (ctx.vars().containsKey(a.id)) {
                    ctx.vars().get(a.id).inner = value;
                } else {
                    ctx.vars().put(a.id, new Value(value));
                }
            }
            case Deref a -> {
                var referent = a.dereference(ctx);
                referent.inner = this.value.eval(ctx).inner;
            }
            default -> throw new LangError(assignee.pos(), "invalid assignee");
        }

        return Value.makeUndefined();
    }
}
