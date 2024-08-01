package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Variable extends Op implements Assignable {
    int id;

    public Variable(Span pos, int id) {
        this.pos = pos;
        this.id = id;
    }

    @Override
    public Value eval(Context ctx) {
        if (ctx.vars().containsKey(id)) {
            return ctx.vars().get(id).move();
        }

        throw error(String.format("variable `%s` is undefined", ctx.symbols().resolve(id)));
    }

    @Override
    public Value referent(Context ctx) {
        if (ctx.vars().containsKey(id)) {
            return ctx.vars().get(id);
        }

        var referent = Value.makeUndefined();
        ctx.vars().put(id, referent);
        return referent;
    }
}
