package com.darnj.op;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public abstract class AssignmentOp extends Op {
    Op assignee;
    Op value;

    public AssignmentOp build(Span pos, Op assignee, Op value) {
        this.pos = pos;
        this.assignee = assignee;
        this.value = value;
        return this;
    }

    abstract void assign(Value assignee, Value value);
    
    @Override
    public Value eval(Context ctx) {
        switch (assignee) {
            case Variable a -> {
                var value = this.value.eval(ctx);

                if (!ctx.vars().containsKey(a.id)) {
                    ctx.vars().put(a.id, Value.makeUndefined());
                }
                assign(ctx.vars().get(a.id), value);
            }
            case Deref a -> {
                var referent = a.dereference(ctx);
                assign(referent, this.value.eval(ctx));
            }
            default -> throw new LangError(assignee.pos(), "invalid assignee");
        }

        return Value.makeUndefined();
    }
}
